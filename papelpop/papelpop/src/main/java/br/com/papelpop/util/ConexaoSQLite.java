package br.com.papelpop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSQLite {

    private static final String URL =
            "jdbc:sqlite:src/main/resources/database/papelpop.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
