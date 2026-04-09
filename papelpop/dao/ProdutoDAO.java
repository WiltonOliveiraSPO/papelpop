package br.com.papelpop.dao;

import br.com.papelpop.model.Produto;
import br.com.papelpop.util.ConexaoSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a classe ProdutoDAO e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class ProdutoDAO {

    // Ação: executa a rotina 'salvar' desta classe.
    public void salvar(Produto p) throws SQLException {
        String sql = """
            INSERT INTO produtos (descricao, preco, ativo)
            VALUES (?, ?, ?)
        """;

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getDescricao());
            ps.setDouble(2, p.getPreco());
            ps.setInt(3, p.isAtivo() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    // Ação: executa a rotina 'atualizar' desta classe.
    public void atualizar(Produto p) throws SQLException {
        String sql = """
            UPDATE produtos
               SET descricao=?, preco=?, ativo=?
             WHERE id_produto=?
        """;

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getDescricao());
            ps.setDouble(2, p.getPreco());
            ps.setInt(3, p.isAtivo() ? 1 : 0);
            ps.setInt(4, p.getIdProduto());
            ps.executeUpdate();
        }
    }

    // Ação: executa a rotina 'excluir' desta classe.
    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id_produto=?";
        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Ação: executa a rotina 'listar' desta classe.
    public List<Produto> listar() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY id_produto";

        try (Connection con = ConexaoSQLite.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setIdProduto(rs.getInt("id_produto"));
                p.setDescricao(rs.getString("descricao"));
                p.setPreco(rs.getDouble("preco"));
                p.setAtivo(rs.getInt("ativo") == 1);
                lista.add(p);
            }
        }
        return lista;
    }
}
