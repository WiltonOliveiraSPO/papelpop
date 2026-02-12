package br.com.papelpop.dao;

import br.com.papelpop.model.Estoque;
import br.com.papelpop.util.ConexaoSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstoqueDAO {

    public List<Estoque> listar() throws SQLException {
        List<Estoque> lista = new ArrayList<>();

        String sql = """
            SELECT e.id_produto, p.descricao, e.quantidade
              FROM estoque e
              JOIN produtos p ON p.id_produto = e.id_produto
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
    public void entrada(int idProduto, int quantidade) throws SQLException {
        String sql = """
            UPDATE estoque
               SET quantidade = quantidade + ?
             WHERE id_produto = ?
        """;

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, quantidade);
            ps.setInt(2, idProduto);
            ps.executeUpdate();
        }
    }
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
            }

            PreparedStatement ps2 = con.prepareStatement(atualiza);
            ps2.setInt(1, quantidade);
            ps2.setInt(2, idProduto);
            ps2.executeUpdate();
        }
    }
    
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