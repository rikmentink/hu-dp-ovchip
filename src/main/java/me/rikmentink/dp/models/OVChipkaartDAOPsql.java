package me.rikmentink.dp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPsql implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAO rdao;
    private ProductDAO pdao;

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
        this.rdao = new ReizigerDAOPsql(conn);
        this.pdao = new ProductDAOPsql(conn);
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
            for (Product product : kaart.getProducten()) {
                PreparedStatement juncStmt = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?)");
                juncStmt.setInt(1, kaart.getKaartnummer());
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
            for (Product product : kaart.getProducten()) {
                PreparedStatement juncStmt = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?)");
                juncStmt.setInt(1, kaart.getKaartnummer());
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

        OVChipkaart kaart = null;
        while (rs.next()) {
            kaart = new OVChipkaart(
                rs.getInt("kaart_nummer"),
                LocalDate.parse(rs.getString("geldig_tot"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                rs.getInt("klasse"),
                rs.getDouble("saldo"),
                rdao.findById(rs.getInt("reiziger_id"))
            );

            List<Product> products = pdao.findByOVChipkaart(kaart);
            kaart.setProducten(products);
        }
        return kaart;
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException {
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

            List<Product> products = pdao.findByOVChipkaart(kaart);
            kaart.setProducten(products);

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

            List<Product> products = pdao.findByOVChipkaart(kaart);
            kaart.setProducten(products);
            
            kaarten.add(kaart);
        }
        return kaarten;
    }
}
