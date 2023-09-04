package me.rikmentink.dp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    private static String DATABASE_URL = "jdbc:postgresql://localhost/hu-dp";
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        conn = getConnection();
        // TODO: Roep test functie aan.
        closeConnection();
    }

    private static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", "rikmentink");
        props.setProperty("password", "wachtwoord");
        return DriverManager.getConnection(DATABASE_URL, props);
    }

    private static void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    // TODO: Implementeer test functie van Canvas
}