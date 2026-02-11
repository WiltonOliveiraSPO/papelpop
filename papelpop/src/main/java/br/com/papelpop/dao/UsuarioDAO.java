package br.com.papelpop.dao;

import br.com.papelpop.model.Usuario;
import br.com.papelpop.util.ConexaoSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void salvar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, login, senha, ativo) VALUES (?, ?, ?, ?)";
        try (Connection c = ConexaoSQLite.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getLogin());
            ps.setString(3, u.getSenha());
            ps.setInt(4, u.isAtivo() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET nome=?, login=?, senha=?, ativo=? WHERE id_usuario=?";
        try (Connection c = ConexaoSQLite.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getLogin());
            ps.setString(3, u.getSenha());
            ps.setInt(4, u.isAtivo() ? 1 : 0);
            ps.setInt(5, u.getIdUsuario());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario=?";
        try (Connection c = ConexaoSQLite.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id_usuario";

        try (Connection c = ConexaoSQLite.conectar();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setSenha(rs.getString("senha"));
                u.setAtivo(rs.getInt("ativo") == 1);
                lista.add(u);
            }
        }
        return lista;
    }
}
