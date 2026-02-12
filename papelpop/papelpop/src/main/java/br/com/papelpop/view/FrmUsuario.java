package br.com.papelpop.view;

import br.com.papelpop.dao.UsuarioDAO;
import br.com.papelpop.model.Usuario;
import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FrmUsuario extends JFrame {

    private JTextField txtId, txtNome, txtLogin;
    private JPasswordField txtSenha;
    private JCheckBox chkAtivo;

    private List<Usuario> usuarios;
    private int posicao = 0;

    private UsuarioDAO dao = new UsuarioDAO();

    public FrmUsuario() {
        setTitle("Cadastro de Usuários");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarBarraBotoes(), BorderLayout.SOUTH);

        carregarUsuarios();
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtId = new JTextField();
        txtId.setEnabled(false);

        txtNome = new JTextField();
        txtLogin = new JTextField();
        txtSenha = new JPasswordField();
        chkAtivo = new JCheckBox("Ativo");

        p.add(new JLabel("Código:"));
        p.add(txtId);
        p.add(new JLabel("Nome:"));
        p.add(txtNome);
        p.add(new JLabel("Login:"));
        p.add(txtLogin);
        p.add(new JLabel("Senha:"));
        p.add(txtSenha);
        p.add(new JLabel(""));
        p.add(chkAtivo);

        return p;
    }

    private JPanel criarBarraBotoes() {
        JPanel p = new JPanel(new GridLayout(2, 4, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.setBackground(TemaPapelPop.FUNDO_CLARO);

        p.add(botao("⏮ Primeiro", e -> primeiro()));
        p.add(botao("◀ Anterior", e -> anterior()));
        p.add(botao("▶ Próximo", e -> proximo()));
        p.add(botao("⏭ Último", e -> ultimo()));

        p.add(botao("➕ Novo", e -> novo()));
        p.add(botao("💾 Salvar", e -> salvar()));
        p.add(botao("✏️ Editar", e -> editar()));
        p.add(botao("🗑 Excluir", e -> excluir()));

        return p;
    }

    private RoundedButton botao(String texto, java.awt.event.ActionListener ac) {
        RoundedButton b = new RoundedButton(texto);
        b.addActionListener(ac);
        return b;
    }

    private void carregarUsuarios() {
        try {
            usuarios = dao.listar();
            if (!usuarios.isEmpty()) {
                posicao = 0;
                mostrar();
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void mostrar() {
        Usuario u = usuarios.get(posicao);
        txtId.setText(String.valueOf(u.getIdUsuario()));
        txtNome.setText(u.getNome());
        txtLogin.setText(u.getLogin());
        txtSenha.setText(u.getSenha());
        chkAtivo.setSelected(u.isAtivo());
    }

    private void primeiro() { posicao = 0; mostrar(); }
    private void ultimo() { posicao = usuarios.size() - 1; mostrar(); }
    private void anterior() { if (posicao > 0) posicao--; mostrar(); }
    private void proximo() { if (posicao < usuarios.size() - 1) posicao++; mostrar(); }

    private void novo() {
        txtId.setText("");
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        chkAtivo.setSelected(true);
    }

    private void salvar() {
        try {
            Usuario u = new Usuario();
            u.setNome(txtNome.getText());
            u.setLogin(txtLogin.getText());
            u.setSenha(new String(txtSenha.getPassword()));
            u.setAtivo(chkAtivo.isSelected());

            dao.salvar(u);
            carregarUsuarios();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void editar() {
        try {
            Usuario u = new Usuario();
            u.setIdUsuario(Integer.parseInt(txtId.getText()));
            u.setNome(txtNome.getText());
            u.setLogin(txtLogin.getText());
            u.setSenha(new String(txtSenha.getPassword()));
            u.setAtivo(chkAtivo.isSelected());

            dao.atualizar(u);
            carregarUsuarios();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void excluir() {
        try {
            dao.excluir(Integer.parseInt(txtId.getText()));
            carregarUsuarios();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void erro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
