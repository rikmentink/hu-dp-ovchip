package me.rikmentink.dp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Main {
    private static String DATABASE_URL = "jdbc:postgresql://localhost/hu-dp";
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        conn = getConnection();
        ResultSet users = getAllUsers(conn);

        System.out.println("Alle reizigers:");
        int count = 1;
        while (users.next()) {
            System.out.println(
                    String.format("  #%s: %s (%s)", count, users.getString("name"), users.getString("birthdate")));   
            count++;
        }

        closeConnection();
    }

    private static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", "rikmentink");
        props.setProperty("password", "test");
        return DriverManager.getConnection(DATABASE_URL, props);
    }

    private static void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    private static ResultSet getAllUsers(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM users");
    }
}