package br.com.papelpop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Representa a classe ConexaoSQLite que é a conexão do sistema com SQLite no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class ConexaoSQLite {

    private static final String DB_RELATIVE_PATH = "src/main/resources/database/papelpop.db";
    private static final String DB_APPDATA_DIR = ".papelpop/database";
    private static final Path DB_PATH = resolverCaminhoBanco();
    private static final String URL = "jdbc:sqlite:" + DB_PATH.toString();
    private static volatile boolean inicializado = false;

    // Ação: executa a rotina 'conectar' desta classe.
    public static Connection conectar() throws SQLException {
        prepararEstruturaBanco();
        Connection conexao = DriverManager.getConnection(URL);
        inicializarSchema(conexao);
        return conexao;
    }

    // Ação: executa a rotina 'resolverCaminhoBanco' desta classe.
    private static Path resolverCaminhoBanco() {
        Path atual = Paths.get("").toAbsolutePath().normalize();
        Path projeto = atual.resolve(DB_RELATIVE_PATH);
        if (Files.exists(projeto.getParent())) {
            return projeto;
        }

        Path projetoEmSubpasta = atual.resolve("papelpop").resolve(DB_RELATIVE_PATH);
        if (Files.exists(projetoEmSubpasta.getParent())) {
            return projetoEmSubpasta;
        }

        Path home = Paths.get(System.getProperty("user.home"));
        Path appData = home.resolve(DB_APPDATA_DIR).resolve("papelpop.db");
        return appData;
    }

    // Ação: executa a rotina 'prepararEstruturaBanco' desta classe.
    private static void prepararEstruturaBanco() throws SQLException {
        try {
            Files.createDirectories(DB_PATH.getParent());
        } catch (Exception e) {
            throw new SQLException("Nao foi possivel preparar diretorio do banco: " + DB_PATH, e);
        }
    }

    // Ação: executa a rotina 'inicializarSchema' desta classe.
    private static void inicializarSchema(Connection conexao) throws SQLException {
        if (inicializado) {
            return;
        }

        synchronized (ConexaoSQLite.class) {
            if (inicializado) {
                return;
            }

            try (Statement st = conexao.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
                st.execute("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        login TEXT NOT NULL UNIQUE,
                        senha TEXT NOT NULL,
                        ativo INTEGER DEFAULT 1
                    )
                """);
                st.execute("""
                    CREATE TABLE IF NOT EXISTS clientes (
                        id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        telefone TEXT,
                        email TEXT,
                        cpf TEXT UNIQUE,
                        data_cadastro DATE DEFAULT CURRENT_DATE
                    )
                """);
                st.execute("""
                    CREATE TABLE IF NOT EXISTS produtos (
                        id_produto INTEGER PRIMARY KEY AUTOINCREMENT,
                        descricao TEXT NOT NULL,
                        preco REAL NOT NULL,
                        ativo INTEGER DEFAULT 1
                    )
                """);
                st.execute("""
                    CREATE TABLE IF NOT EXISTS estoque (
                        id_produto INTEGER PRIMARY KEY,
                        quantidade INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
                    )
                """);
                st.execute("""
                    CREATE TABLE IF NOT EXISTS vendas (
                        id_venda INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_cliente INTEGER,
                        id_usuario INTEGER NOT NULL,
                        data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,
                        total REAL NOT NULL,
                        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
                        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
                    )
                """);
                st.execute("""
                    CREATE TABLE IF NOT EXISTS venda_itens (
                        id_item INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_venda INTEGER NOT NULL,
                        id_produto INTEGER NOT NULL,
                        quantidade INTEGER NOT NULL,
                        preco_unit REAL NOT NULL,
                        subtotal REAL NOT NULL,
                        FOREIGN KEY (id_venda) REFERENCES vendas(id_venda),
                        FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
                    )
                """);
            }

            garantirUsuarioPadrao(conexao);

            inicializado = true;
        }
    }

    // Ação: executa a rotina 'garantirUsuarioPadrao' desta classe.
    private static void garantirUsuarioPadrao(Connection conexao) throws SQLException {
        String verificaSql = "SELECT 1 FROM usuarios WHERE login = ?";
        String insereSql = "INSERT INTO usuarios (nome, login, senha, ativo) VALUES (?, ?, ?, 1)";

        try (PreparedStatement psVerifica = conexao.prepareStatement(verificaSql)) {
            psVerifica.setString(1, "admin");
            ResultSet rs = psVerifica.executeQuery();

            if (!rs.next()) {
                try (PreparedStatement psInsere = conexao.prepareStatement(insereSql)) {
                    psInsere.setString(1, "Administrador");
                    psInsere.setString(2, "admin");
                    psInsere.setString(3, "1234");
                    psInsere.executeUpdate();
                }
            }
        }
    }
}
