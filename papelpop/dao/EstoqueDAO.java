package br.com.papelpop.dao;

import br.com.papelpop.model.Estoque;
import br.com.papelpop.util.ConexaoSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a classe EstoqueDAO e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class EstoqueDAO {

    // Ação: executa a rotina 'listar' desta classe.
    public List<Estoque> listar() throws SQLException {
        List<Estoque> lista = new ArrayList<>();

        String sql = """
            SELECT p.id_produto,
                   p.descricao,
                   COALESCE(e.quantidade, 0) AS quantidade
              FROM produtos p
              LEFT JOIN estoque e ON e.id_produto = p.id_produto
             WHERE p.ativo = 1
             ORDER BY p.descricao
        """;

        try (Connection con = ConexaoSQLite.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Estoque e = new Estoque();
                e.setIdProduto(rs.getInt("id_produto"));
                e.setDescricaoProduto(rs.getString("descricao"));
                e.setQuantidade(rs.getInt("quantidade"));
                lista.add(e);
            }
        }
        return lista;
    }
    // Ação: executa a rotina 'entrada' desta classe.
    public void entrada(int idProduto, int quantidade) throws SQLException {
        String sql = """
            INSERT INTO estoque (id_produto, quantidade)
            VALUES (?, ?)
            ON CONFLICT(id_produto) DO UPDATE
               SET quantidade = quantidade + excluded.quantidade
        """;

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProduto);
            ps.setInt(2, quantidade);
            ps.executeUpdate();
        }
    }
    // Ação: executa a rotina 'saida' desta classe.
    public void saida(int idProduto, int quantidade) throws SQLException {
        String verifica = "SELECT quantidade FROM estoque WHERE id_produto=?";
        String atualiza = """
            UPDATE estoque
               SET quantidade = quantidade - ?
             WHERE id_produto = ?
        """;

        try (Connection con = ConexaoSQLite.conectar()) {

            PreparedStatement ps1 = con.prepareStatement(verifica);
            ps1.setInt(1, idProduto);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int atual = rs.getInt("quantidade");

                if (atual < quantidade) {
                    throw new SQLException("Estoque insuficiente!");
                }
            } else {
                throw new SQLException("Produto sem registro de estoque.");
            }

            PreparedStatement ps2 = con.prepareStatement(atualiza);
            ps2.setInt(1, quantidade);
            ps2.setInt(2, idProduto);
            ps2.executeUpdate();
        }
    }
    
    // Ação: executa a rotina 'criarEstoqueSeNaoExistir' desta classe.
    public void criarEstoqueSeNaoExistir(int idProduto) throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO estoque (id_produto, quantidade)
            VALUES (?, 0)
        """;

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProduto);
            ps.executeUpdate();
        }
    }
    
    // Ação: executa a rotina 'buscarQuantidade' desta classe.
    public int buscarQuantidade(int idProduto) throws SQLException {
        String sql = "SELECT quantidade FROM estoque WHERE id_produto=?";

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProduto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantidade");
            }
        }
        return 0;
    }



}
