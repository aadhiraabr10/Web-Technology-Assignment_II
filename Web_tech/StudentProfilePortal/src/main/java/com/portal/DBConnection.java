package com.portal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3308/student_portal"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Kolkata";
    private static final String DB_URL = System.getenv().getOrDefault("STUDENT_PORTAL_DB_URL", DEFAULT_DB_URL);
    private static final String DB_USER = System.getenv().getOrDefault("STUDENT_PORTAL_DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("STUDENT_PORTAL_DB_PASSWORD", "");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found in classpath.", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
