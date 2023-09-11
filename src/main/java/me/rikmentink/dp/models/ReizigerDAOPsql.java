package me.rikmentink.dp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPsql implements ReizigerDAO {
    private Connection conn;
    private AdresDAO adao;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
        this.adao = new AdresDAOPsql(conn);
    }

    @Override
    public boolean save(Reiziger reiziger) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO reiziger VALUES (?,?,?,?,?)");
        stmt.setInt(1, reiziger.getId());
        stmt.setString(2, reiziger.getVoorletter());
        stmt.setString(3, reiziger.getTussenvoegsel());
        stmt.setString(4, reiziger.getAchternaam());
        stmt.setObject(5, reiziger.getGeboortedatum());

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected > 0 && reiziger.getAdres() != null) {
            adao.save(reiziger.getAdres(), reiziger.getId());
        }
        
        return rowsAffected > 0;
    }

    @Override
    public boolean update(Reiziger reiziger) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE reiziger SET reiziger_id = ?, voorletters = ?, tussenvoegsel = ?, achternaam = ?, geboortedatum = ? WHERE reiziger_id = ?");
        stmt.setInt(1, reiziger.getId());
        stmt.setString(2, reiziger.getVoorletter());
        stmt.setString(3, reiziger.getTussenvoegsel());
        stmt.setString(4, reiziger.getAchternaam());
        stmt.setObject(5, reiziger.getGeboortedatum());
        stmt.setInt(6, reiziger.getId());

        if (reiziger.getAdres() != null) {
            adao.update(reiziger.getAdres());
        }

        return stmt.executeUpdate() > 0;
    }

    @Override
    public boolean delete(Reiziger reiziger) throws SQLException {
        System.out.println("Deleting.");
        if (reiziger.getAdres() != null) {
            System.out.println("reiziger has adres");
            adao.delete(reiziger.getAdres());
        }

        PreparedStatement stmt = conn.prepareStatement("DELETE FROM reiziger WHERE reiziger_id = ?");
        stmt.setInt(1, reiziger.getId());
        return stmt.execute();
    }

    @Override
    public Reiziger findById(int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reiziger WHERE reiziger_id = ?");
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        Reiziger reiziger = null;
        while (rs.next()) {
            reiziger = new Reiziger(
                rs.getInt("reiziger_id"),
                rs.getString("voorletters"),
                rs.getString("tussenvoegsel"),
                rs.getString("achternaam"),
                LocalDate.parse(rs.getString("geboortedatum"), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                adao.findByReiziger(reiziger)
            );
            reiziger.setAdres(adao.findByReiziger(reiziger));
        }
        return reiziger;
    }

    @Override
    public List<Reiziger> findByGbdatum(String datum) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reiziger WHERE geboortedatum = ?");
        stmt.setString(1, datum);
        ResultSet rs = stmt.executeQuery();

        List<Reiziger> reizigers = new ArrayList<Reiziger>();
        while (rs.next()) {
            Reiziger reiziger = new Reiziger(
                rs.getInt("reiziger_id"),
                rs.getString("voorletters"),
                rs.getString("tussenvoegsel"),
                rs.getString("achternaam"),
                LocalDate.parse(rs.getString("geboortedatum"), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            reiziger.setAdres(adao.findByReiziger(reiziger));

            reizigers.add(reiziger);
        }
        return reizigers;

    }

    @Override
    public List<Reiziger> findAll() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reiziger");
        ResultSet rs = stmt.executeQuery();

        List<Reiziger> reizigers = new ArrayList<Reiziger>();
        while (rs.next()) {
            Reiziger reiziger = new Reiziger(
                rs.getInt("reiziger_id"),
                rs.getString("voorletters"),
                rs.getString("tussenvoegsel"),
                rs.getString("achternaam"),
                LocalDate.parse(rs.getString("geboortedatum"), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );
            reiziger.setAdres(adao.findByReiziger(reiziger));
            
            reizigers.add(reiziger);
        }
        return reizigers;
    }
}
