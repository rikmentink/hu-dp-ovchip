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

    public OVChipkaartDAOPsql(Connection conn) {
        this.conn = conn;
        this.rdao = new ReizigerDAOPsql(conn);
    }

    @Override
    public boolean save(OVChipkaart kaart) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO ov_chipkaart VALUES (?,?,?,?,?)");
        stmt.setInt(1, kaart.getKaartnummer());
        stmt.setObject(2, kaart.getGeldigTot());
        stmt.setInt(3, kaart.getKlasse());
        stmt.setDouble(4, kaart.getSaldo());
        stmt.setInt(5, kaart.getReizigerId());
        return stmt.execute();
    }

    @Override
    public boolean update(OVChipkaart kaart) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE ov_chipkaart SET geldig_tot = ?, klasse = ?, saldo = ?, reiziger_id = ? WHERE kaart_nummer = ?");
        stmt.setObject(1, kaart.getGeldigTot());
        stmt.setInt(2, kaart.getKlasse());
        stmt.setDouble(3, kaart.getSaldo());
        stmt.setInt(4, kaart.getReizigerId());
        stmt.setInt(5, kaart.getKaartnummer());
        return stmt.executeUpdate() > 0;
    }

    @Override
    public boolean delete(OVChipkaart kaart) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM ov_chipkaart WHERE kaart_nummer = ?");
        stmt.setInt(1, kaart.getKaartnummer());
        return stmt.execute();
    }

    @Override
    public OVChipkaart findById(int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ov_chipkaart WHERE kaart_nummer = ?");
        stmt.setInt(1, id);
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
            kaarten.add(kaart);
        }
        return kaarten;
    }
}
