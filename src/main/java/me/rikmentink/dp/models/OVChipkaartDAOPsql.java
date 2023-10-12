package me.rikmentink.dp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAO rdao;
    private ProductDAO pdao;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
        this.rdao = new ReizigerDAOPsql(conn);
    }

    public void setProductDAO(ProductDAO pdao) {
        this.pdao = pdao;
    }

    @Override
    public boolean save(OVChipkaart kaart) throws SQLException {
        try {
            // Disable automatic commits to execute both queries at the same time.
            conn.setAutoCommit(false);

            PreparedStatement kaartStmt = conn.prepareStatement("INSERT INTO ov_chipkaart VALUES (?,?,?,?,?)");
            kaartStmt.setInt(1, kaart.getKaartnummer());
            kaartStmt.setObject(2, kaart.getGeldigTot());
            kaartStmt.setInt(3, kaart.getKlasse());
            kaartStmt.setDouble(4, kaart.getSaldo());
            kaartStmt.setInt(5, kaart.getReizigerId());
            kaartStmt.execute();

            // Insert associations to products for the newly created OV-chipkaart
            for (int productNummer : kaart.getProducten()) {
                PreparedStatement juncStmt = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?)");
                juncStmt.setInt(1, kaart.getKaartnummer());
                juncStmt.setInt(2, productNummer);
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
    public boolean update(OVChipkaart kaart) throws SQLException {
        try {
            // Disable automatic commits to execute both queries at the same time.
            conn.setAutoCommit(false);

            PreparedStatement kaartStmt = conn.prepareStatement("UPDATE ov_chipkaart SET geldig_tot = ?, klasse = ?, saldo = ?, reiziger_id = ? WHERE kaart_nummer = ?");
            kaartStmt.setObject(1, kaart.getGeldigTot());
            kaartStmt.setInt(2, kaart.getKlasse());
            kaartStmt.setDouble(3, kaart.getSaldo());
            kaartStmt.setInt(4, kaart.getReizigerId());
            kaartStmt.setInt(5, kaart.getKaartnummer());
            kaartStmt.executeUpdate();

            // Remove existing associations for this OVChipkaart
            PreparedStatement juncDelStmt = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?");
            juncDelStmt.setInt(1, kaart.getKaartnummer());
            juncDelStmt.execute();

             // Re-insert associations for the updated OVChipkaart
            for (int productNummer : kaart.getProducten()) {
                PreparedStatement juncStmt = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?)");
                juncStmt.setInt(1, kaart.getKaartnummer());
                juncStmt.setInt(2, productNummer);
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
    public boolean delete(OVChipkaart kaart) throws SQLException {
        try {
            // Disable automatic commits to execute both queries at the same time.
            conn.setAutoCommit(false);
        
            // Remove existing associations for this OVChipkaart
            PreparedStatement juncDelStmt = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE kaart_nummer = ?");
            juncDelStmt.setInt(1, kaart.getKaartnummer());
            juncDelStmt.execute();

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM ov_chipkaart WHERE kaart_nummer = ?");
            stmt.setInt(1, kaart.getKaartnummer());
            stmt.execute();

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
    public OVChipkaart findByKaartnummer(int kaartnummer) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?");
        stmt.setInt(1, kaartnummer);

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            OVChipkaart kaart = new OVChipkaart(
                rs.getInt("kaart_nummer"),
                LocalDate.parse(rs.getString("geldig_tot"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                rs.getInt("klasse"),
                rs.getDouble("saldo"),
                rdao.findById(rs.getInt("reiziger_id"))
            );

            PreparedStatement prodStmt = conn.prepareStatement("SELECT product_nummer FROM ov_chipkaart_product WHERE kaart_nummer = ?");
            prodStmt.setInt(1, kaart.getKaartnummer());

            ResultSet prodRs = prodStmt.executeQuery();
            List<Integer> productIds = new ArrayList<>();
            while (prodRs.next()) {
                productIds.add(prodRs.getInt("product_nummer"));
            }

            kaart.setProducten(productIds);
            return kaart;
        }
        return null;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException {
        if (reiziger == null) return new ArrayList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ov_chipkaart WHERE reiziger_id = ?");
        stmt.setInt(1, reiziger.getId());

        ResultSet rs = stmt.executeQuery();
        List<OVChipkaart> kaarten = new ArrayList<OVChipkaart>();
        while (rs.next()) {
            OVChipkaart kaart = new OVChipkaart(
                rs.getInt("kaart_nummer"),
                LocalDate.parse(rs.getString("geldig_tot"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                rs.getInt("klasse"),
                rs.getDouble("saldo"),
                rdao.findById(rs.getInt("reiziger_id"))
            );

            PreparedStatement prodStmt = conn.prepareStatement("SELECT product_nummer FROM ov_chipkaart_product WHERE kaart_nummer = ?");
            prodStmt.setInt(1, kaart.getKaartnummer());

            ResultSet prodRs = prodStmt.executeQuery();
            List<Integer> productIds = new ArrayList<>();
            while (prodRs.next()) {
                productIds.add(prodRs.getInt("product_nummer"));
            }

            kaart.setProducten(productIds);
            kaarten.add(kaart);
        }
        return kaarten;
    }

    @Override
    public List<OVChipkaart> findByProduct(Product product) throws SQLException {
        if (product == null) return new ArrayList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT k.kaart_nummer, k.geldig_tot, k.klasse, k.saldo, k.reiziger_id FROM ov_chipkaart k JOIN ov_chipkaart_product kp ON k.kaart_nummer = kp.kaart_nummer WHERE kp.product_nummer = ?");
        stmt.setInt(1, product.getProductNummer());
        
        ResultSet rs = stmt.executeQuery();
        List<OVChipkaart> kaarten = new ArrayList<OVChipkaart>();
        while (rs.next()) {
            OVChipkaart kaart = new OVChipkaart(
                rs.getInt("kaart_nummer"),
                LocalDate.parse(rs.getString("geldig_tot"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                rs.getInt("klasse"),
                rs.getDouble("saldo"),
                rdao.findById(rs.getInt("reiziger_id"))
            );

            PreparedStatement prodStmt = conn.prepareStatement("SELECT product_nummer FROM ov_chipkaart_product WHERE kaart_nummer = ?");
            prodStmt.setInt(1, kaart.getKaartnummer());

            ResultSet prodRs = prodStmt.executeQuery();
            List<Integer> productIds = new ArrayList<>();
            while (prodRs.next()) {
                productIds.add(prodRs.getInt("product_nummer"));
            }

            kaart.setProducten(productIds);
            kaarten.add(kaart);
        }

        return kaarten;
    }

    @Override
    public List<OVChipkaart> findAll() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ov_chipkaart");

        ResultSet rs = stmt.executeQuery();
        List<OVChipkaart> kaarten = new ArrayList<OVChipkaart>();
        while (rs.next()) {
            OVChipkaart kaart = new OVChipkaart(
                rs.getInt("kaart_nummer"),
                LocalDate.parse(rs.getString("geldig_tot"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                rs.getInt("klasse"),
                rs.getDouble("saldo"),
                rdao.findById(rs.getInt("reiziger_id"))
            );

            PreparedStatement prodStmt = conn.prepareStatement("SELECT product_nummer FROM ov_chipkaart_product WHERE kaart_nummer = ?");
            prodStmt.setInt(1, kaart.getKaartnummer());
            
            ResultSet prodRs = prodStmt.executeQuery();
            List<Integer> productIds = new ArrayList<>();
            while (prodRs.next()) {
                productIds.add(prodRs.getInt("product_nummer"));
            }

            kaart.setProducten(productIds);
            kaarten.add(kaart);
        }

        return kaarten;
    }
}