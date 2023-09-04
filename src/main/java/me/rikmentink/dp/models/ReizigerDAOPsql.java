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

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Reiziger reiziger) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO reiziger VALUES (?,?,?,?,?)");
        stmt.setInt(1, reiziger.getId());
        stmt.setString(2, reiziger.getVoorletters());
        stmt.setString(3, reiziger.getTussenvoegsel());
        stmt.setString(4, reiziger.getAchternaam());
        stmt.setString(5, reiziger.getGeboortedatum().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return stmt.execute();
    }

    @Override
    public boolean update(Reiziger reiziger) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE"); // TODO: Juiste update statement maken.
        stmt.setInt(1, reiziger.getId());
        stmt.setString(2, reiziger.getVoorletters());
        stmt.setString(3, reiziger.getTussenvoegsel());
        stmt.setString(4, reiziger.getAchternaam());
        stmt.setObject(5, reiziger.getGeboortedatum());
        return stmt.executeUpdate() > 0;
    }

    @Override
    public boolean delete(Reiziger reiziger) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM reiziger WHERE id = ?");
        stmt.setInt(1, reiziger.getId());
        return stmt.execute();
    }

    @Override
    public List<Reiziger> findById(int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reiziger WHERE id = ?");
        stmt.setInt(1, id);
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
            reizigers.add(reiziger);
        }
        return reizigers;
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
            reizigers.add(reiziger);
        }
        return reizigers;
    }
}
