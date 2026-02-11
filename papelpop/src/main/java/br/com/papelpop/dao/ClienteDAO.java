package br.com.papelpop.dao;

import br.com.papelpop.model.Cliente;
import br.com.papelpop.util.ConexaoSQLite;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void salvar(Cliente c) throws SQLException {
        String sql = """
            INSERT INTO clientes (nome, telefone, email, cpf)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNome());
            ps.setString(2, c.getTelefone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getCpf());
            ps.executeUpdate();
        }
    }

    public void atualizar(Cliente c) throws SQLException {
        String sql = """
            UPDATE clientes
               SET nome=?, telefone=?, email=?, cpf=?
             WHERE id_cliente=?
        """;

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getNome());
            ps.setString(2, c.getTelefone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getCpf());
            ps.setInt(5, c.getIdCliente());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente=?";
        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY id_cliente";

        try (Connection con = ConexaoSQLite.conectar();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNome(rs.getString("nome"));
                c.setTelefone(rs.getString("telefone"));
                c.setEmail(rs.getString("email"));
                c.setCpf(rs.getString("cpf"));

                String dataStr = rs.getString("data_cadastro");
                if (dataStr != null && !dataStr.isEmpty()) {
                    c.setDataCadastro(LocalDate.parse(dataStr));
                }



                lista.add(c);
            }
        }
        return lista;
    }
}
