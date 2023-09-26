package me.rikmentink.dp.models;

import java.sql.SQLException;
import java.util.List;

public interface OVChipkaartDAO {
    public boolean save(OVChipkaart kaart) throws SQLException;
    public boolean update(OVChipkaart kaart) throws SQLException;
    public boolean delete(OVChipkaart kaart) throws SQLException;
    public OVChipkaart findByKaartnummer(int kaartnummer) throws SQLException;
    public List<OVChipkaart> findByReiziger(Reiziger reiziger) throws SQLException;
    public List<OVChipkaart> findAll() throws SQLException;
}
