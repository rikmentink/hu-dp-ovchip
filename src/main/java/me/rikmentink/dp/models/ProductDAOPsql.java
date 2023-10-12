package me.rikmentink.dp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;
    private OVChipkaartDAO odao;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
    }

    public void setKaartDAO(OVChipkaartDAO odao) {
        this.odao = odao;
    }

    @Override
    public boolean save(Product product) throws SQLException {
        try {
            // Disable automatic commits to execute both queries at the same time.
            conn.setAutoCommit(false);

            PreparedStatement productStmt = conn.prepareStatement("INSERT INTO product VALUES (?,?,?,?)");
            productStmt.setInt(1, product.getProductNummer());
            productStmt.setString(2, product.getNaam());
            productStmt.setString(3, product.getBeschrijving());
            productStmt.setDouble(4, product.getPrijs());
            productStmt.execute();

            // Insert associations to OV-chipkaarten for the newly created product
            for (int kaartNummer : product.getKaarten()) {
                if (!this.checkAssociationExists(kaartNummer, product.getProductNummer())) {
                    PreparedStatement juncStmt = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?)");
                    juncStmt.setInt(1, kaartNummer);
                    juncStmt.setInt(2, product.getProductNummer());
                    juncStmt.execute();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            // If one of the statements fail, rollback their changes.
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean update(Product product) throws SQLException {
        try {
            // Disable automatic commits to execute both queries at the same time.
            conn.setAutoCommit(false);

            PreparedStatement productStmt = conn
                .prepareStatement("UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?");
            productStmt.setString(1, product.getNaam());
            productStmt.setString(2, product.getBeschrijving());
            productStmt.setDouble(3, product.getPrijs());
            productStmt.setInt(4, product.getProductNummer());
            productStmt.executeUpdate();

            // Remove existing associations for this product
            PreparedStatement juncDelStmt = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE product_nummer = ?");    
            juncDelStmt.setInt(1, product.getProductNummer());
            juncDelStmt.execute();

            // Re-insert associations for the updated product
            for (int kaartNummer : product.getKaarten()) {
                PreparedStatement juncStmt = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?)");
                juncStmt.setInt(1, kaartNummer);
                juncStmt.setInt(2, product.getProductNummer());
                juncStmt.execute();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            // If one of the statements fail, rollback their changes.
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public boolean delete(Product product) throws SQLException {
        try {
            // Disable automatic commits to execute both queries at the same time.
            conn.setAutoCommit(false);
            
            // Remove existing associations for this product
            PreparedStatement juncStmt = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE product_nummer = ?");
            juncStmt.setInt(1, product.getProductNummer());
            juncStmt.execute();

            PreparedStatement productStmt = conn.prepareStatement("DELETE FROM product WHERE product_nummer = ?");
            productStmt.setInt(1, product.getProductNummer());
            productStmt.execute();

            conn.commit();
            return true;
        } catch (SQLException e) {
            // If one of the statements fail, rollback their changes.
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public Product findByProductNummer(int productNummer) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product WHERE product_nummer = ?");
        stmt.setInt(1, productNummer);
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Product product = new Product(
                rs.getInt("product_nummer"),
                rs.getString("naam"),
                rs.getString("beschrijving"),
                rs.getDouble("prijs"));
            
            PreparedStatement kaartStmt = conn.prepareStatement("SELECT kaart_nummer FROM ov_chipkaart_product WHERE product_nummer = ?");
            kaartStmt.setInt(1, product.getProductNummer());
        
            ResultSet kaartRs = kaartStmt.executeQuery();
            List<Integer> kaartIds = new ArrayList<>();
            while (kaartRs.next()) {
                kaartIds.add(kaartRs.getInt("product_nummer"));
            }
        
            product.setKaarten(kaartIds);
            return product;
        }

        return null;
    }

    @Override
    public List<Product> findByOVChipkaart(OVChipkaart kaart) throws SQLException {
        if (kaart == null) return new ArrayList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT p.product_nummer, p.naam, p.beschrijving, p.prijs FROM product p JOIN ov_chipkaart_product kp ON p.product_nummer = kp.product_nummer WHERE kp.kaart_nummer = ?");
        stmt.setInt(1, kaart.getKaartnummer());
        ResultSet rs = stmt.executeQuery();

        List<Product> producten = new ArrayList<Product>();
        while (rs.next()) {
            Product product = new Product(
                    rs.getInt("product_nummer"),
                    rs.getString("naam"),
                    rs.getString("beschrijving"),
                    rs.getDouble("prijs"));
            
            PreparedStatement kaartStmt = conn.prepareStatement("SELECT kaart_nummer FROM ov_chipkaart_product WHERE product_nummer = ?");
            kaartStmt.setInt(1, product.getProductNummer());
        
            ResultSet kaartRs = kaartStmt.executeQuery();
            List<Integer> kaartIds = new ArrayList<>();
            while (kaartRs.next()) {
                kaartIds.add(kaartRs.getInt("kaart_nummer"));
            }
        
            product.setKaarten(kaartIds);
            producten.add(product);
        }

        return producten;
    }

    @Override
    public List<Product> findAll() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM product");
        ResultSet rs = stmt.executeQuery();

        List<Product> producten = new ArrayList<Product>();
        while (rs.next()) {
            Product product = new Product(
                    rs.getInt("product_nummer"),
                    rs.getString("naam"),
                    rs.getString("beschrijving"),
                    rs.getDouble("prijs"));

            PreparedStatement kaartStmt = conn.prepareStatement("SELECT kaart_nummer FROM ov_chipkaart_product WHERE product_nummer = ?");
            kaartStmt.setInt(1, product.getProductNummer());
        
            ResultSet kaartRs = kaartStmt.executeQuery();
            List<Integer> kaartIds = new ArrayList<>();
            while (kaartRs.next()) {
                kaartIds.add(kaartRs.getInt("kaart_nummer"));
            }
        
            product.setKaarten(kaartIds);
            producten.add(product);
        }
        return producten;
    }

    private boolean checkAssociationExists(int kaartNummer, int productNummer) throws SQLException {
        PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM ov_chipkaart_product WHERE kaart_nummer = ? AND product_nummer = ?");
        checkStmt.setInt(1, kaartNummer);
        checkStmt.setInt(2, productNummer);

        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }
}
