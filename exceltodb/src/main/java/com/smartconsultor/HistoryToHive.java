package com.smartconsultor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HistoryToHive {
    public static void main(String[] args) {
        String postgresUrl = "jdbc:postgresql://localhost:5432/your_database";
        String postgresUser = "your_user";
        String postgresPassword = "your_password";

        String hiveUrl = "jdbc:hive2://localhost:10000/default";
        String hiveUser = "hive_user";
        String hivePassword = "hive_password";

        String tablesQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'history'";

        try (Connection postgresConnection = DriverManager.getConnection(postgresUrl, postgresUser, postgresPassword);
             PreparedStatement tablesStmt = postgresConnection.prepareStatement(tablesQuery);
             ResultSet tablesResultSet = tablesStmt.executeQuery()) {

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("table_name");

                String fieldsQuery = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = 'history' AND table_name = ?";
                StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");

                try (PreparedStatement fieldsStmt = postgresConnection.prepareStatement(fieldsQuery)) {
                    fieldsStmt.setString(1, tableName);

                    try (ResultSet fieldsResultSet = fieldsStmt.executeQuery()) {
                        boolean first = true;
                        while (fieldsResultSet.next()) {
                            if (!first) {
                                createTableQuery.append(", ");
                            }
                            String columnName = fieldsResultSet.getString("column_name");
                            String columnType = mapFieldType(fieldsResultSet.getString("data_type"));
                            createTableQuery.append(columnName).append(" ").append(columnType);
                            first = false;
                        }
                    }
                }

                createTableQuery.append(")");

                try (Connection hiveConnection = DriverManager.getConnection(hiveUrl, hiveUser, hivePassword);
                     PreparedStatement hiveStmt = hiveConnection.prepareStatement(createTableQuery.toString())) {
                    hiveStmt.executeUpdate();
                    System.out.println("Table created in Hive: " + tableName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String mapFieldType(String postgresFieldType) {
        // Map PostgreSQL types to Hive types
        switch (postgresFieldType.toLowerCase()) {
            case "integer":
            case "int":
                return "INT";
            case "character varying":
            case "varchar":
            case "text":
                return "STRING";
            case "boolean":
                return "BOOLEAN";
            case "bigint":
                return "BIGINT";
            case "jsonb":
            case "json":
                return "STRING"; // Hive doesn't support JSONB directly
            case "date":
                return "DATE";
            case "timestamp":
            case "timestamp without time zone":
                return "TIMESTAMP";
            default:
                return "STRING"; // Default to STRING for unsupported types
        }    
    }
}