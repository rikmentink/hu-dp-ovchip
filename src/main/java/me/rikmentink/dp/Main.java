package me.rikmentink.dp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

import me.rikmentink.dp.models.Adres;
import me.rikmentink.dp.models.AdresDAO;
import me.rikmentink.dp.models.AdresDAOPsql;
import me.rikmentink.dp.models.Reiziger;
import me.rikmentink.dp.models.ReizigerDAO;
import me.rikmentink.dp.models.ReizigerDAOPsql;

public class Main {
    private static String DATABASE_URL = "jdbc:postgresql://localhost/hu-dp";
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        conn = getConnection();

        ReizigerDAO rdao = new ReizigerDAOPsql(conn);
        AdresDAO adao = new AdresDAOPsql(conn);
        
        testReizigerDAO(rdao);
        testAdresDAO(rdao, adao);

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

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", LocalDate.parse("1981-03-14", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");

        // Verander de zojuist gemaakte reiziger en persisteer
        System.out.print("[Test] Eerst was de naam " + sietske.getNaam());
        sietske.setAchternaam("Jansen");
        rdao.update(sietske);
        sietske = rdao.findById(sietske.getId());
        System.out.println(", na ReizigerDAO.update() is de naam " + sietske.getNaam());

        // Verwijder de zojuist gemaakte reiziger en persisteer
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.delete() ");
        rdao.delete(sietske);
        reizigers = rdao.findAll();
        System.out.print(reizigers.size() + " reizigers");
    }

    private static void testAdresDAO(ReizigerDAO rdao, AdresDAO adao) throws SQLException {
        System.out.println("\n---------- Test AdresDAO -------------");

        // Haal alle adressen op uit de database
        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        for (Adres a : adressen) {
            System.out.println(a);
        }

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        Reiziger reiziger = new Reiziger(77, "S", "", "Boers", LocalDate.parse("1981-03-14", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        rdao.save(reiziger);

        // Maak een nieuwe adres aan en persisteer deze in de database
        Adres adres = new Adres(12, "3584 CS", "15", "Heidelberglaan", "Utrecht", reiziger);
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.save() ");
        adao.save(adres);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen\n");

        // Verander het zojuist gemaakte adres en persisteer
        System.out.print("[Test] Eerst was de postcode " + adres.getPostcode());
        adres.setPostcode("3846 EG");
        adao.update(adres);
        adres = adao.findById(adres.getId());
        System.out.println(", na AdresDAO.update() is de postcode " + adres.getPostcode());

        // Verwijder de zojuist gemaakte reiziger en persisteer
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.delete() ");
        adao.delete(adres);
        adressen = adao.findAll();
        System.out.print(adressen.size() + " adressen");
        
        rdao.delete(reiziger);
    }
}