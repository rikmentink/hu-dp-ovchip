package me.rikmentink.dp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Main {
    private static String DATABASE_URL = "jdbc:postgresql://localhost/hu-dp";

    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            ResultSet users = getAllUsers(conn);

            System.out.println("Alle reizigers:");
            int count = 1;
            while (users.next()) {
                System.out.println(
                        String.format("  #%s: %s (%s)", count, users.getString("name"), users.getString("birthdate")));   
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", "rikmentink");
        props.setProperty("password", "test");
        return DriverManager.getConnection(DATABASE_URL, props);
    }

    private static ResultSet getAllUsers(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM users");
    }
}