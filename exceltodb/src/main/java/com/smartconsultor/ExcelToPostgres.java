package com.smartconsultor;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExcelToPostgres {

    public static void main(String[] args) {
        if (args.length < 8) {
            printUsage();
            System.exit(1);
        }

        // Đọc tham số từ dòng lệnh
        Map<String, String> params = parseArgs(args);

        String excelFilePath = params.get("path");
        String version = params.get("version");
        String dbUrl = params.get("dbUrl");
        String dbUser = params.get("dbUser");

        if (excelFilePath == null || version == null || dbUrl == null || dbUser == null) {
            System.err.println("Missing required arguments.");
            printUsage();
            System.exit(1);
        }

        // Nhập mật khẩu ở chế độ ẩn
        Console console = System.console();
        if (console == null) {
            System.err.println("No console available");
            System.exit(1);
        }

        char[] passwordChars = console.readPassword("Enter database password: ");
        String dbPassword = new String(passwordChars);

        try (FileInputStream fis = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            FileWriter logWriter = new FileWriter("execution_log.txt", true)) {

            logWriter.write(getCurrentTime() + " Processing Excel file version: " + version + "\n");

            // Lặp qua các sheet (bỏ qua sheet(0))
            for (int i = 1; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                logWriter.write(getCurrentTime() + " Processing sheet: " + sheet.getSheetName() + "\n");

                // Lặp qua các dòng từ dòng 1 trở đi
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) continue;

                    Cell cell = row.getCell(4); // Cột E là cột số 4 (index từ 0)
                    if (cell == null) continue;

                    String sql = cell.getStringCellValue().trim();
                    if (!sql.isEmpty()) {
                        try (Statement stmt = connection.createStatement()) {
                            int affectedRows = stmt.executeUpdate(sql); // Thực thi câu SQL và lấy số bản ghi bị ảnh hưởng
                            logWriter.write(getCurrentTime() + " Executed: " + sql + "\n");
                            logWriter.write(getCurrentTime() + " Rows affected: " + affectedRows + "\n");
                        } catch (Exception e) {
                            logWriter.write(getCurrentTime() + " Error executing SQL: " + sql + "\n");
                            logWriter.write(getCurrentTime() + " " + e.toString() + "\n");
                        }
                    }
                }
            }
            logWriter.write(getCurrentTime() + " Processing complete for version: " + version + "\n");

        } catch (IOException e) {
            System.err.println("Error reading Excel file.");
            System.err.println(e.toString());
        } catch (Exception e) {
            System.err.println("Database connection error.");
            System.err.println(e.toString());
        }
    }

    private static void printUsage() {
        System.err.println("Usage: java -jar exceltodb-1.0-SNAPSHOT.jar --path [excelFilePath] --version [version] --dbUrl [dbUrl] --dbUser [dbUser]");
        System.err.println("Example: java -jar exceltodb-1.0-SNAPSHOT.jar --path data.xlsx --version v1 --dbUrl jdbc:postgresql://localhost:5432/smartconsultor --dbUser smartconsultor");
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length && args[i].startsWith("--")) {
                params.put(args[i].substring(2), args[i + 1]);
            }
        }
        return params;
    }

    // Helper function to get current timestamp
    private static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}
