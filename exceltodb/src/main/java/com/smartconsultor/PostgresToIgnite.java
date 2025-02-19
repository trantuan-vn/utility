package com.smartconsultor;

import org.apache.ignite.Ignition;
import org.apache.ignite.client.ClientException;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.ClientConfiguration;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import java.sql.*;
import java.util.*;

public class PostgresToIgnite {
    public static void main(String[] args) {
        ClientConfiguration cfg = new ClientConfiguration().setAddresses("localhost:10800");
        cfg.setUserName("ignite");
        cfg.setUserPassword("ignite");
        String jdbcUrl = "jdbc:postgresql://localhost:5432/smartconsultor";
        String user = "smartconsultor";
        String password = "secret99";
        IgniteClient igniteClient = null;
        Connection conn = null;

        try  
        {
            igniteClient = Ignition.startClient(cfg);
            conn = DriverManager.getConnection(jdbcUrl, user, password);            

            System.out.println("\n>>> Synchronizing PostgreSQL schema with Apache Ignite.");

            // Fetch all tables in schema system_parameters
            String fetchTablesQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'system_parameters'";
            System.out.println("fetchTablesQuery query: " + fetchTablesQuery);
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(fetchTablesQuery)) {
                while (rs.next()) {
                    String tableName = rs.getString("table_name");
                    
                    // Fetch column definitions for this table
                    String fetchColumnsQuery = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = 'system_parameters' AND table_name = '" + tableName + "'";
                    System.out.println("fetchColumnsQuery query: " + fetchColumnsQuery);
                    Map<String, String> columnMap = new LinkedHashMap<>();
                    try (Statement colStmt = conn.createStatement(); ResultSet colRs = colStmt.executeQuery(fetchColumnsQuery)) {
                        while (colRs.next()) {
                            String columnName = colRs.getString("column_name").toUpperCase();
                            String columnType = mapPostgresToIgniteDataType(colRs.getString("data_type"));
                            columnMap.put(columnName, columnType);
                        }
                    }

                    // Fetch primary key for this table
                    String fetchPrimaryKeyQuery = "SELECT kcu.column_name FROM information_schema.key_column_usage kcu " +
                            "JOIN information_schema.table_constraints tc ON tc.constraint_name = kcu.constraint_name " +
                            "WHERE tc.table_schema = 'system_parameters' AND tc.table_name = '" + tableName + "' AND tc.constraint_type = 'PRIMARY KEY'";
                    System.out.println("fetchPrimaryKeyQuery query: " + fetchPrimaryKeyQuery);                            
                    List<String> primaryKeys = new ArrayList<>();
                    try (Statement pkStmt = conn.createStatement(); ResultSet pkRs = pkStmt.executeQuery(fetchPrimaryKeyQuery)) {
                        while (pkRs.next()) {
                            primaryKeys.add(pkRs.getString("column_name"));
                        }
                    }

                    // Check if table exists in Ignite
                    boolean tableExists = checkTableExists(igniteClient, tableName.toUpperCase());
                    
                    if (!tableExists) {
                        createTableStructure(igniteClient, tableName.toUpperCase(), primaryKeys, columnMap);
                    } else {
                        // Alter table if column changes are detected
                        updateTableStructure(igniteClient, tableName.toUpperCase(), primaryKeys , columnMap);
                    }
                    migrateDataToIgnite(igniteClient, conn, tableName, columnMap);                    
                }
            }
        } catch (ClientException | SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("PostgreSQL connection closed.");
                }
                if (igniteClient != null) {
                    igniteClient.close();
                    System.out.println("Ignite client connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing PostgreSQL connection: " + e.getMessage());
            }
        }
    }

    private static boolean checkTableExists(IgniteClient igniteClient, String tableName) {
        try {
            String query = "SELECT count(*) FROM SYS.TABLES WHERE TABLE_NAME = '" + tableName + "'";
            System.out.println("TableExists query: " + query);
            List<List<?>> result = igniteClient.query(new SqlFieldsQuery(query)).getAll();
            return !result.isEmpty() && ((Long) result.get(0).get(0)) > 0;
        } catch (Exception e) {
            return false;
        }
    }
    private static void createTableStructure(IgniteClient igniteClient, String tableName, List<String> primaryKeys, Map<String, String> columnMap) {
        try {
            // Create table if it does not exist
            StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + tableName + " (");
            for (Map.Entry<String, String> entry : columnMap.entrySet()) {
                createTableQuery.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
            }
            if (!primaryKeys.isEmpty()) {
                createTableQuery.append("PRIMARY KEY (").append(String.join(", ", primaryKeys)).append(")");
            } else {
                createTableQuery.setLength(createTableQuery.length() - 2);
            }
            createTableQuery.append(")");

            System.out.println("createTableQuery query: " + createTableQuery);
            igniteClient.query(new SqlFieldsQuery(createTableQuery.toString())).getAll();

        } catch (Exception e) {
            System.err.println("Error updating table structure for " + tableName + ": " + e.getMessage());
        }
    }

    private static void updateTableStructure(IgniteClient igniteClient, String tableName, List<String> primaryKeys, Map<String, String> newColumns) {
        try {
            String fetchCurrentColumnsQuery = "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + tableName + "' and not COLUMN_NAME in ('_KEY','_VAL')";
            System.out.println("fetchCurrentColumnsQuery query: " + fetchCurrentColumnsQuery);
            List<List<?>> result = igniteClient.query(new SqlFieldsQuery(fetchCurrentColumnsQuery)).getAll();
            
            Map<String, String> existingColumns = new HashMap<>();
            for (List<?> row : result) {
                existingColumns.put((String) row.get(0), mapJdbcType( (int) row.get(1)));
            }
            
            for (Map.Entry<String, String> entry : newColumns.entrySet()) {
                String columnName = entry.getKey();
                String columnType = entry.getValue();
                if (!existingColumns.containsKey(columnName)) {
                    String alterAddQuery = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType;
                    System.out.println("alterAddQuery query: " + alterAddQuery);
                    igniteClient.query(new SqlFieldsQuery(alterAddQuery)).getAll();
                } else if (!existingColumns.get(columnName).equalsIgnoreCase(columnType)) {
                    String dropQuery = "DROP TABLE " + tableName;
                    System.out.println("dropQuery query: " + dropQuery);
                    igniteClient.query(new SqlFieldsQuery(dropQuery)).getAll();
                    createTableStructure(igniteClient, tableName.toUpperCase(), primaryKeys, newColumns);
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating table structure for " + tableName + ": " + e.getMessage());
        }
    }
    private static void migrateDataToIgnite(IgniteClient igniteClient, Connection conn, String tableName, Map<String, String> columnMap) {
        String columnNames = String.join(", ", columnMap.keySet());
        String fetchDataQuery = "SELECT " + columnNames + " FROM system_parameters." + tableName;

        try (PreparedStatement pstmt = conn.prepareStatement(fetchDataQuery); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                List<Object> values = new ArrayList<>();
                for (String column : columnMap.keySet()) {
                    values.add(rs.getObject(column));
                }
                
                String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
                String insertQuery = "INSERT INTO " + tableName.toUpperCase() + " (" + columnNames + ") VALUES (" + placeholders + ")";
                
                SqlFieldsQuery sql = new SqlFieldsQuery(insertQuery).setArgs(values.toArray());
                System.out.println("insertQuery query: " + sql.getSql());
                igniteClient.query(sql).getAll();
            }
        } catch (SQLException e) {
            System.err.println("Error fetching data for " + tableName + ": " + e.getMessage());
        }
    }

    private static String mapPostgresToIgniteDataType(String postgresDataType) {
        switch (postgresDataType.toLowerCase()) {
            case "integer": case "int": case "int4": return "INTEGER";
            case "bigint": case "int8": return "BIGINT";
            case "smallint": case "int2": return "SMALLINT";
            case "text": case "varchar": case "char": case "character varying": return "VARCHAR";
            case "boolean": case "bool": return "BOOLEAN";
            case "bytea": return "BINARY";
            case "float": case "float4": return "FLOAT";
            case "double precision": case "float8": return "DOUBLE";
            case "date": return "DATE";
            case "timestamp": case "timestamp without time zone": return "TIMESTAMP";
            case "timestamptz": case "timestamp with time zone": return "TIMESTAMP";
            case "numeric": case "decimal": return "DECIMAL";
            case "jsonb": return "VARCHAR";
            default: throw new IllegalArgumentException("Unsupported PostgreSQL data type: " + postgresDataType);
        }
    }
    private static String mapJdbcType(int jdbcType) {
        switch (jdbcType) {
            case java.sql.Types.INTEGER: return "INTEGER";
            case java.sql.Types.VARCHAR: return "VARCHAR";
            case java.sql.Types.BIGINT: return "BIGINT";
            case java.sql.Types.BOOLEAN: return "BOOLEAN";
            case java.sql.Types.DOUBLE: return "DOUBLE";
            case java.sql.Types.FLOAT: return "FLOAT";
            case java.sql.Types.DATE: return "DATE";
            case java.sql.Types.TIMESTAMP: return "TIMESTAMP";
            case java.sql.Types.DECIMAL: return "DECIMAL";
            case java.sql.Types.CHAR: return "CHAR";
            case java.sql.Types.SMALLINT: return "SMALLINT";
            case java.sql.Types.TINYINT: return "TINYINT";
            case java.sql.Types.REAL: return "REAL";
            case java.sql.Types.BINARY: return "BINARY";
            case java.sql.Types.VARBINARY: return "VARBINARY";
            case java.sql.Types.LONGVARCHAR: return "LONGVARCHAR";
            case java.sql.Types.LONGVARBINARY: return "LONGVARBINARY";
            case java.sql.Types.NUMERIC: return "NUMERIC";
            case java.sql.Types.OTHER: return "OTHER"; // Thường dùng cho JSONB, JSON
            default: return "UNKNOWN (" + jdbcType + ")";
        }
    }    
}
