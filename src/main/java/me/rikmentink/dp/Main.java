package me.rikmentink.dp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

import me.rikmentink.dp.models.*;

public class Main {
    private static String DATABASE_URL = "jdbc:postgresql://localhost/hu-dp";
    private static Connection conn;

    public static void main(String[] args) throws SQLException {
        conn = getConnection();

        ReizigerDAO rdao = new ReizigerDAOPsql(conn);
        AdresDAO adao = new AdresDAOPsql(conn);
        OVChipkaartDAO odao = new OVChipkaartDAOPsql(conn);
        ProductDAO pdao = new ProductDAOPsql(conn);
        odao.setProductDAO(pdao);
        pdao.setKaartDAO(odao);
        
        testReizigerDAO(rdao);
        testAdresDAO(rdao, adao);
        testOVChipkaartDAO(rdao, odao);
        testProductDAO(pdao);
        testOVChipkaartProduct(rdao, odao, pdao);

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
        System.out.println(reizigers.size() + " reizigers");

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
        System.out.println("\n\n---------- Test AdresDAO -------------");

        // Haal alle adressen op uit de database
        List<Adres> adressen = adao.findAll();
        System.out.println("[Test] AdresDAO.findAll() geeft de volgende adressen:");
        for (Adres a : adressen) {
            System.out.println(String.format("Reiziger {%s, Adres {%s}}", a.getReiziger(), a));
        }

        // Maak een nieuwe reiziger aan en persisteer deze in de database
        Reiziger reiziger = new Reiziger(77, "S", "", "Boers", LocalDate.parse("1981-03-14", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        rdao.save(reiziger);

        // Maak een nieuwe adres aan en persisteer deze in de database
        Adres adres = new Adres(12, "3584 CS", "15", "Heidelberglaan", "Utrecht", reiziger);
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.save() ");
        adao.save(adres);
        adressen = adao.findAll();
        System.out.println(adressen.size() + " adressen");

        // Verander het zojuist gemaakte adres en persisteer
        System.out.print("[Test] Eerst was de postcode " + adres.getPostcode());
        adres.setPostcode("3846 EG");
        adao.update(adres);
        adres = adao.findById(adres.getId());
        System.out.println(", na AdresDAO.update() is de postcode " + adres.getPostcode());

        // Verwijder het zojuist gemaakte adres en persisteer
        System.out.print("[Test] Eerst " + adressen.size() + " adressen, na AdresDAO.delete() ");
        adao.delete(adres);
        adressen = adao.findAll();
        System.out.print(adressen.size() + " adressen");

        // Verwijder de tijdelijk gemaakte reiziger
        rdao.delete(reiziger);
    }

    private static void testOVChipkaartDAO(ReizigerDAO rdao, OVChipkaartDAO odao) throws SQLException {
        System.out.println("\n\n---------- Test OVChipkaartDAO -------------");

        // Haal alle OV-chipkaarten op uit de database
        List<OVChipkaart> kaarten = odao.findAll();
        System.out.println("[Test] OVChipkaartDAO.findAll() geeft de volgende OV-chipkaarten:");
        for (OVChipkaart k : kaarten) {
            System.out.println(k);
        }

        // Maak een nieuwe OV-chipkaart aan en persisteer deze in de database
        Reiziger reiziger = new Reiziger(6, "R", "", "Mentink", LocalDate.of(2004, Month.JULY, 15));
        OVChipkaart kaart = new OVChipkaart(69, LocalDate.of(2023, Month.SEPTEMBER, 16), 1, 30.5, reiziger);
        System.out.print("[Test] Eerst " + kaarten.size() + " OV-chipkaarten, na OVChipkaartDAO.save() ");
        rdao.save(reiziger);
        odao.save(kaart);
        kaarten = odao.findAll();
        System.out.println(kaarten.size() + " kaarten");

        // Verander de zojuist gemaakte OV-chipkaart en persisteer
        System.out.print("[Test] Eerst was het saldo " + kaart.getSaldo());
        kaart.setSaldo(100.0);
        odao.update(kaart);
        kaart = odao.findByKaartnummer(kaart.getKaartnummer());
        System.out.println(", na OVChipkaartDAO.update() is het saldo " + kaart.getSaldo());

        // Verwijder de zojuist gemaakte reiziger en persisteer
        System.out.print("[Test] Eerst " + kaarten.size() + " OV-chipkaarten, na OVChipkaartDAO.delete() ");
        odao.delete(kaart);
        kaarten = odao.findAll();
        System.out.println(kaarten.size() + " OV-chipkaarten");

        // Verwijder de tijdelijk gemaakte reiziger
        rdao.delete(reiziger);
    }

    private static void testProductDAO(ProductDAO pdao) throws SQLException {
        System.out.println("\n\n---------- Test ProductDAO -------------");

        // Haal alle producten op uit de database
        List<Product> producten = pdao.findAll();
        System.out.println("[Test] ProductDAO.findAll() geeft de volgende producten:");
        for (Product p : producten) {
            System.out.println(p);
        }

        // Maak een nieuwe producten aan en persisteer deze in de database
        Product product = new Product(10, "Jongerendagkaart", "Voordelig een hele dag reizen voor jongeren.", 7.50);
        System.out.print("[Test] Eerst " + producten.size() + " producten, na ProductDAO.save() ");
        pdao.save(product);
        producten = pdao.findAll();
        System.out.println(producten.size() + " producten");

        // Verander de zojuist gemaakte OV-chipkaart en persisteer
        System.out.print("[Test] Eerst was de prijs " + product.getPrijs());
        product.setPrijs(10.0);
        pdao.update(product);
        product = pdao.findByProductNummer(product.getProductNummer());
        System.out.println(", na ProductDAO.update() is de prijs " + product.getPrijs());

        // Verwijder de zojuist gemaakte reiziger en persisteer
        System.out.print("[Test] Eerst " + producten.size() + " producten, na ProductDAO.delete() ");
        pdao.delete(product);
        producten = pdao.findAll();
        System.out.println(producten.size() + " producten");
    }

    private static void testOVChipkaartProduct(ReizigerDAO rdao, OVChipkaartDAO odao, ProductDAO pdao) throws SQLException {
        System.out.println("\n\n---------- Test OVChipkaart-Product Relatie -------------");

        // Maak een nieuwe OV-chipkaart aan met een reiziger en persisteer        
        Reiziger reiziger = new Reiziger(7, "R", "", "Mentink", LocalDate.of(2004, Month.JULY, 15));
        OVChipkaart kaart = new OVChipkaart(1001, LocalDate.now().plusYears(1), 2, 50.0, reiziger);
        System.out.print("[Test] Eerst geen producten gekoppeld aan OVChipkaart ");
        rdao.save(reiziger);
        odao.save(kaart);
        List<Product> products = pdao.findByOVChipkaart(kaart);
        System.out.println("[" + products.size() + " producten]");

        // Maak nieuwe producten aan en koppel ze aan de OV-chipkaart
        Product product = new Product(10, "Jongerendagkaart", "Voordelig een hele dag reizen voor jongeren.", 7.50);
        Product product2 = new Product(11, "NS Flex", "Geen idee, maar het heeft irritante reclames.", 30.0);
        product.addKaart(kaart);
        product2.addKaart(kaart);

        // Persisteer de zojuist gemaakte producten
        System.out.println("\n[Test] Eerst " + products.size() + " producten, na ProductDAO.save():");
        pdao.save(product);
        pdao.save(product2);
        products = pdao.findByOVChipkaart(kaart);
        
        int i = 0;
        for (Product p : products) {
            System.out.println(i+1 + ": " + p);
            i++;
        }  
        
        // Pas de producten van de OV-chipkaart aan en persisteer
        System.out.println("\n[Test] Eerst " + products.size() + " producten, na OVChipkaartDAO.update():");
        kaart.removeProduct(product);
        odao.update(kaart);
        products = pdao.findByOVChipkaart(kaart);

        i = 0;
        for (Product p : products) {
            System.out.println(i+1 + ": " + p);
            i++;
        }  

        // Verwijder een product dat gekoppeld is aan een OV-chipkaart en persisteer
        System.out.print("\n[Test] Eerst " + products.size() + " producten, na ProductDAO.delete() ");
        pdao.delete(product2);
        products = pdao.findByOVChipkaart(kaart);
        System.out.println("[" + products.size() + " producten]");
        
        // Verwijder de tijdelijk gemaakte reiziger, OV-chipkaart en product
        pdao.delete(product);
        odao.delete(kaart);
        rdao.delete(reiziger);
    }
}