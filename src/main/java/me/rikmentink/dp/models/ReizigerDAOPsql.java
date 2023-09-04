package me.rikmentink.dp.models;

import java.sql.Connection;

public class ReizigerDAOPsql {
    private Connection conn;

    public ReizigerDAOPsql(Connection conn) {
        this.conn = conn;
    }
}
