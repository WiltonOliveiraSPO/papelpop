package br.com.papelpop.dao;

import br.com.papelpop.model.Venda;
import br.com.papelpop.model.VendaItem;
import br.com.papelpop.util.ConexaoSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    public void salvarVenda(Venda venda) throws SQLException {

        String sqlVenda = """
            INSERT INTO vendas (id_cliente, id_usuario, total)
            VALUES (?, ?, ?)
        """;

        String sqlItem = """
            INSERT INTO venda_itens
            (id_venda, id_produto, quantidade, preco_unit, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;

        String sqlEstoque = """
            UPDATE estoque
               SET quantidade = quantidade - ?
             WHERE id_produto = ?
        """;

        try (Connection con = ConexaoSQLite.conectar()) {
            con.setAutoCommit(false);

            // 1️⃣ INSERE VENDA
            PreparedStatement psVenda = con.prepareStatement(
                    sqlVenda, Statement.RETURN_GENERATED_KEYS);

            psVenda.setInt(1, venda.getIdCliente());
            psVenda.setInt(2, venda.getIdUsuario());
            psVenda.setDouble(3, venda.getTotal());
            psVenda.executeUpdate();

            ResultSet rs = psVenda.getGeneratedKeys();
            rs.next();
            int idVenda = rs.getInt(1);

            // 2️⃣ INSERE ITENS + ATUALIZA ESTOQUE
            for (VendaItem item : venda.getItens()) {

                PreparedStatement psItem = con.prepareStatement(sqlItem);
                psItem.setInt(1, idVenda);
                psItem.setInt(2, item.getIdProduto());
                psItem.setInt(3, item.getQuantidade());
                psItem.setDouble(4, item.getPrecoUnit());
                psItem.setDouble(5, item.getSubtotal());
                psItem.executeUpdate();

                PreparedStatement psEst = con.prepareStatement(sqlEstoque);
                psEst.setInt(1, item.getQuantidade());
                psEst.setInt(2, item.getIdProduto());
                psEst.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            throw e;
        }
    }
    
    public void excluir(int idVenda) throws SQLException {

        String deleteItens = "DELETE FROM venda_itens WHERE id_venda = ?";
        String deleteVenda = "DELETE FROM vendas WHERE id_venda = ?";

        try (Connection con = ConexaoSQLite.conectar()) {

            con.setAutoCommit(false);

            try (PreparedStatement psItens = con.prepareStatement(deleteItens);
                 PreparedStatement psVenda = con.prepareStatement(deleteVenda)) {

                psItens.setInt(1, idVenda);
                psItens.executeUpdate();

                psVenda.setInt(1, idVenda);
                psVenda.executeUpdate();

                con.commit();

            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Venda> listar() throws SQLException {

        List<Venda> lista = new ArrayList<>();

        String sql = """
            SELECT v.id_venda, v.id_cliente, v.id_usuario, 
                   v.data_venda, v.total
            FROM vendas v
            ORDER BY v.id_venda
        """;

        try (Connection con = ConexaoSQLite.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Venda v = new Venda();
                v.setIdVenda(rs.getInt("id_venda"));
                v.setIdCliente(rs.getInt("id_cliente"));
                v.setIdUsuario(rs.getInt("id_usuario"));
                v.setDataVenda(
                	    rs.getTimestamp("data_venda").toLocalDateTime());
                v.setTotal(rs.getDouble("total"));

                // carregar itens da venda
                v.setItens(listarItens(v.getIdVenda()));

                lista.add(v);
            }
        }

        return lista;
    }
    
    private List<VendaItem> listarItens(int idVenda) throws SQLException {

        List<VendaItem> lista = new ArrayList<>();

        String sql = """
            SELECT vi.id_item, vi.id_produto, p.descricao,
                   vi.quantidade, vi.preco_unit, vi.subtotal
            FROM venda_itens vi
            JOIN produtos p ON p.id_produto = vi.id_produto
            WHERE vi.id_venda = ?
        """;

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idVenda);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                VendaItem item = new VendaItem();
                //item.setIdItem(rs.getInt("id_item"));
                item.setIdProduto(rs.getInt("id_produto"));
                item.setDescricaoProduto(rs.getString("descricao"));
                item.setQuantidade(rs.getInt("quantidade"));
                item.setPrecoUnit(rs.getDouble("preco_unit"));
                item.setSubtotal(rs.getDouble("subtotal"));

                lista.add(item);
            }
        }

        return lista;
    }

    

}
