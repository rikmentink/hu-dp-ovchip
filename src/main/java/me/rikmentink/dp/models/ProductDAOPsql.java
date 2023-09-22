package me.rikmentink.dp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPsql implements ProductDAO {
    private Connection conn;

    public ProductDAOPsql(Connection conn) {
        this.conn = conn;
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

            PreparedStatement juncStmt = conn.prepareStatement("INSERT INTO ov_chipkaart_product VALUES (?, ?)");
            juncStmt.setInt(1, product.getKaartNummer());
            juncStmt.setInt(2, product.getProductNummer());
            juncStmt.execute();

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
        PreparedStatement stmt = conn
                .prepareStatement("UPDATE product SET naam = ?, beschrijving = ?, prijs = ? WHERE product_nummer = ?");
        stmt.setString(1, product.getNaam());
        stmt.setString(2, product.getBeschrijving());
        stmt.setDouble(3, product.getPrijs());
        stmt.setInt(4, product.getProductNummer());
        return stmt.executeUpdate() > 0;
    }

    @Override
    public boolean delete(Product product) throws SQLException {
        try {
            conn.setAutoCommit(false);

            PreparedStatement productStmt = conn.prepareStatement("DELETE FROM product WHERE product_nummer = ?");
            productStmt.setInt(1, product.getProductNummer());
            productStmt.execute();

            PreparedStatement juncStmt = conn.prepareStatement("DELETE FROM ov_chipkaart_product WHERE product_nummer = ?");
            juncStmt.setInt(1, product.getKaartNummer());
            juncStmt.execute();

            conn.commit();
            return true;
        } catch (SQLException e) {
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

        Product product = null;
        while (rs.next()) {
            product = new Product(
                    rs.getInt("product_nummer"),
                    rs.getString("naam"),
                    rs.getString("beschrijving"),
                    rs.getDouble("prijs"));
        }
        return product;
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
            producten.add(product);
        }
        return producten;
    }
}