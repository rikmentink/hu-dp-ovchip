package me.rikmentink.dp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

import me.rikmentink.dp.models.Reiziger;
import me.rikmentink.dp.models.ReizigerDAO;
import me.rikmentink.dp.models.ReizigerDAOPsql;

public class Main {
    private static String DATABASE_URL = "jdbc:postgresql://localhost/hu-dp";
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        conn = getConnection();

        ReizigerDAO rdao = new ReizigerDAOPsql(conn);
        testReizigerDAO(rdao);
        
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

    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        // Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.parse(gbdatum, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Verander de zojuist gemaakte reiziger en persisteer
        System.out.print("[Test] Eerst was de naam " + sietske.getNaam());
        sietske.setAchternaam("Jansen");
        rdao.update(sietske);
        sietske = rdao.findById(sietske.getId()).get(0);
        System.out.println(", na ReizigerDAO.update() is de naam " + sietske.getNaam());

        // Verwijder de zojuist gemaakte reiziger en persisteer
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.delete() ");
        rdao.delete(sietske);
        reizigers = rdao.findAll();
        System.out.print(reizigers.size() + " reizigers");
    }
}