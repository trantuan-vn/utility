package com.smartconsultor;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.*;
import org.apache.spark.sql.SparkSession;

import java.sql.*;
import java.util.*;

public class PostgresToPulsar {
    private static final String PULSAR_SERVICE_URL = "pulsar://pulsar-broker:6650";
    private static final String PULSAR_ADMIN_URL = "http://pulsar-broker:8080";
    private static final String POSTGRES_URL = "jdbc:postgresql://citus-master:5432/smartconsultor";
    private static final String POSTGRES_USER = "smartconsultor";
    private static final String POSTGRES_PASSWORD = "secret99";

    public static void main(String[] args) {
        try {
            System.out.println("Starting PostgresToPulsar application...");
            SparkSession spark = SparkSession.builder().appName("Java Spark Hive").enableHiveSupport().getOrCreate();
            PulsarClient pulsarClient = PulsarClient.builder().serviceUrl(PULSAR_SERVICE_URL).build();
            PulsarAdmin pulsarAdmin = PulsarAdmin.builder().serviceHttpUrl(PULSAR_ADMIN_URL).build();
            Connection postgresConnection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);

            Statement postgresStatement = postgresConnection.createStatement();
            ResultSet resultSet = postgresStatement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'history'");

            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                String topicToday = "persistent://public/today/" + tableName;
                String topicHistory = "persistent://public/history/" + tableName;

                if (!pulsarAdmin.topics().getList("public/today").contains(topicToday)) {
                    pulsarClient.newProducer().topic(topicToday).create().close();
                }
                if (!pulsarAdmin.topics().getList("public/history").contains(topicHistory)) {
                    pulsarClient.newProducer().topic(topicHistory).create().close();
                }

                Map<String, String> postgresSchema = getTableSchema(postgresConnection, tableName);
                Map<String, String> hiveSchema = getTableSchemaFromHive(spark, tableName);
                updateHiveTable(spark, postgresConnection, tableName, postgresSchema, hiveSchema);
            }

            resultSet.close();
            postgresStatement.close();
            postgresConnection.close();
            pulsarClient.close();
            pulsarAdmin.close();
            spark.stop();
            System.out.println("Synchronization completed successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> getTableSchema(Connection conn, String tableName) throws Exception {
        Map<String, String> schema = new LinkedHashMap<>();
        String query = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = 'history' AND table_name = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, tableName);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            schema.put(rs.getString("column_name"), mapPostgresToHiveDataType(rs.getString("data_type")));
        }
        rs.close();
        stmt.close();
        return schema;
    }

    private static Map<String, String> getTableSchemaFromHive(SparkSession spark, String tableName) throws Exception {
        Map<String, String> schema = new LinkedHashMap<>();
        spark.sql("DESCRIBE " + tableName).collectAsList().forEach(row -> schema.put(row.getString(0), row.getString(1)));
        return schema;
    }

    private static void updateHiveTable(SparkSession spark, Connection conn, String tableName, Map<String, String> pgSchema, Map<String, String> hiveSchema) throws Exception {
        if (hiveSchema.isEmpty()) {
            createHiveTable(spark, conn, tableName, pgSchema);
        } else {
            for (Map.Entry<String, String> column : pgSchema.entrySet()) {
                String columnName = column.getKey();
                String columnType = column.getValue();
    
                if (!hiveSchema.containsKey(columnName)) {
                    // Thêm cột mới vào bảng Hive nếu chưa tồn tại
                    spark.sql("ALTER TABLE " + tableName + " ADD COLUMNS (" + columnName + " " + columnType + ")");
                    System.out.println("Added column " + columnName + " to Hive table " + tableName);
                } else {
                    // Nếu cột đã tồn tại nhưng kiểu dữ liệu khác, thực hiện MODIFY
                    String existingType = hiveSchema.get(columnName);
                    if (!existingType.equalsIgnoreCase(columnType)) {
                        spark.sql("ALTER TABLE " + tableName + " CHANGE COLUMN " + columnName + " " + columnName + " " + columnType);
                        System.out.println("Modified column " + columnName + " in Hive table " + tableName + " from " + existingType + " to " + columnType);
                    }
                }
            }
        }
    }
    private static List<String> getBloomFilterColumns(Connection postgresConnection, String tableName) throws Exception {
        String bloomFilterQuery = "SELECT bf.field_id, f.field_name " +
                                  "FROM system_parameters.business_table_uniques bf " +
                                  "JOIN system_parameters.business_tables bt ON bf.table_id = bt.table_id " +
                                  "JOIN system_parameters.business_fields f ON bf.field_id = f.field_id " +
                                  "WHERE bt.table_name = ?";
        PreparedStatement bloomStatement = postgresConnection.prepareStatement(bloomFilterQuery);
        bloomStatement.setString(1, tableName);
        ResultSet bloomResultSet = bloomStatement.executeQuery();
        
        List<String> bloomColumns = new ArrayList<>();
        while (bloomResultSet.next()) {
            bloomColumns.add(bloomResultSet.getString("field_name"));
        }
        bloomResultSet.close();
        bloomStatement.close();
        return bloomColumns;
    }
    
    private static String getPrimaryKeyField(Connection postgresConnection, String tableName) throws Exception {
        String primaryKeyQuery = "SELECT f.field_name " +
                                 "FROM system_parameters.business_tables bt, system_parameters.business_fields f " +
                                 "WHERE bt.table_name = ? and bt.table_id = f.table_id and f.is_primary_key=true";
        PreparedStatement primaryKeyStatement = postgresConnection.prepareStatement(primaryKeyQuery);
        primaryKeyStatement.setString(1, tableName);
        ResultSet primaryKeyResultSet = primaryKeyStatement.executeQuery();
        
        String primaryKeyField = tableName + "_id"; // Giá trị mặc định nếu không tìm thấy khóa chính
        if (primaryKeyResultSet.next()) {
            primaryKeyField = primaryKeyResultSet.getString("field_name");
        }
        primaryKeyResultSet.close();
        primaryKeyStatement.close();
        return primaryKeyField;
    }
    
    private static void createHiveTable(SparkSession spark, Connection conn, String tableName, Map<String, String> schema) throws Exception {
        StringBuilder createTableSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        schema.forEach((col, type) -> createTableSQL.append(col).append(" ").append(type).append(", "));
        createTableSQL.setLength(createTableSQL.length() - 2);
        
        List<String> bloomColumns = getBloomFilterColumns(conn, tableName);
        String primaryKeyField = getPrimaryKeyField(conn, tableName);
        
        // Hoàn thiện câu lệnh tạo bảng
        createTableSQL.append(") ")
                      .append("PARTITIONED BY (business_year INT, business_month INT) ")
                      .append("CLUSTERED BY (").append(primaryKeyField).append(") INTO 8 BUCKETS ")
                      .append("STORED AS ORC ")
                      .append("TBLPROPERTIES (")
                      .append("\"orc.compress\"=\"SNAPPY\", ")
                      .append("\"auto.purge\"=\"true\", ")
                      .append("\"orc.create.index\"=\"true\"");

        if (!bloomColumns.isEmpty()) {
            createTableSQL.append(", \"orc.bloom.filter.columns\"=\"")
                          .append(String.join(",", bloomColumns))
                          .append("\", \"orc.bloom.filter.fpp\"=\"0.05\"");
        }
        createTableSQL.append(")");

        System.out.println("Executing Hive create table query: " + createTableSQL.toString());
        spark.sql(createTableSQL.toString());
        System.out.println("Hive table created successfully for table: " + tableName);
    }

    private static String mapPostgresToHiveDataType(String postgresDataType) {
        switch (postgresDataType.toLowerCase()) {
            case "integer": case "int": case "int4": return "INT";
            case "bigint": case "int8": return "BIGINT";
            case "smallint": case "int2": return "SMALLINT";
            case "text": case "varchar": case "char": case "string": return "STRING";
            case "boolean": case "bool": return "BOOLEAN";
            case "float": case "float4": return "FLOAT";
            case "double precision": case "float8": return "DOUBLE";
            case "date": return "DATE";
            case "timestamp": case "timestamptz": return "TIMESTAMP";
            case "numeric": case "decimal": return "DECIMAL";
            default: throw new IllegalArgumentException("Unsupported PostgreSQL data type: " + postgresDataType);
        }
    }
}
