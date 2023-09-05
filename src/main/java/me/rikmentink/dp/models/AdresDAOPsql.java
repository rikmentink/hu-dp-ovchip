package me.rikmentink.dp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPsql implements AdresDAO {
    private Connection conn;
    private ReizigerDAO rdao;

    public AdresDAOPsql(Connection conn) {
        this.conn = conn;
        rdao = new ReizigerDAOPsql(conn);
    }

    @Override
    public boolean save(Adres adres) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO adres VALUES (?,?,?,?,?,?)");
        stmt.setInt(1, adres.getId());
        stmt.setString(2, adres.getPostcode());
        stmt.setString(3, adres.getHuisnummer());
        stmt.setString(4, adres.getStraat());
        stmt.setString(5, adres.getWoonplaats());
        stmt.setInt(6, adres.getReiziger().getId());
        return stmt.execute();
    }

    @Override
    public boolean update(Adres adres) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "UPDATE adres SET postcode = ?, huisnummer = ?, straat = ?, woonplaats = ? WHERE adres_id = ?");
        stmt.setString(1, adres.getPostcode());
        stmt.setString(2, adres.getHuisnummer());
        stmt.setString(3, adres.getStraat());
        stmt.setString(4, adres.getWoonplaats());
        stmt.setInt(5, adres.getId());
        return stmt.executeUpdate() > 0;
    }

    @Override
    public boolean delete(Adres adres) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM adres WHERE adres_id = ?");
        stmt.setInt(1, adres.getId());
        return stmt.execute();
    }

    @Override
    public Adres findById(int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM adres WHERE adres_id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        Adres adres = null;
        while (rs.next()) {
            adres = new Adres(
                    rs.getInt("adres_id"),
                    rs.getString("postcode"),
                    rs.getString("huisnummer"),
                    rs.getString("straat"),
                    rs.getString("woonplaats"),
                    rdao.findById(rs.getInt("reiziger_id"))
            );
        }
        return adres;
    }

    @Override
    public Adres findByReiziger(Reiziger reiziger) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM adres WHERE reiziger_id = ?");
        stmt.setInt(1, reiziger.getId());
        ResultSet rs = stmt.executeQuery();

        Adres adres = null;
        while (rs.next()) {
            adres = new Adres(
                    rs.getInt("adres_id"),
                    rs.getString("postcode"),
                    rs.getString("huisnummer"),
                    rs.getString("straat"),
                    rs.getString("woonplaats"),
                    reiziger);
        }
        return adres;
    }

    @Override
    public List<Adres> findAll() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM adres");
        ResultSet rs = stmt.executeQuery();

        List<Adres> adressen = new ArrayList<Adres>();
        while (rs.next()) {
            Adres adres = new Adres(
                    rs.getInt("adres_id"),
                    rs.getString("postcode"),
                    rs.getString("huisnummer"),
                    rs.getString("straat"),
                    rs.getString("woonplaats"),
                    rdao.findById(rs.getInt("reiziger_id"))
            );
            adressen.add(adres);
        }
        return adressen;
    }
}
