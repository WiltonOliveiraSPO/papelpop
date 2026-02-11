package br.com.papelpop.dao;

import br.com.papelpop.model.Venda;
import br.com.papelpop.model.VendaItem;
import br.com.papelpop.util.ConexaoSQLite;

import java.sql.*;

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
}
