package me.rikmentink.dp.models;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    public void setKaartDAO(OVChipkaartDAO odao);
    public boolean save(Product product) throws SQLException;
    public boolean update(Product product) throws SQLException;
    public boolean delete(Product product) throws SQLException;
    public Product findByProductNummer(int productNummer) throws SQLException;
    public List<Product> findByOVChipkaart(OVChipkaart kaart) throws SQLException;
    public List<Product> findAll() throws SQLException;
}
