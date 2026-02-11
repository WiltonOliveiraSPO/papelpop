package br.com.papelpop.view;

import br.com.papelpop.dao.ClienteDAO;
import br.com.papelpop.model.Cliente;
import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FrmCliente extends JFrame {

    private JTextField txtId, txtNome, txtTelefone, txtEmail, txtCpf, txtData;
    private List<Cliente> clientes;
    private int posicao = 0;

    private ClienteDAO dao = new ClienteDAO();

    public FrmCliente() {
        setTitle("Cadastro de Clientes");
        setSize(600, 380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarBarraBotoes(), BorderLayout.SOUTH);

        carregarClientes();
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtId = new JTextField();
        txtId.setEnabled(false);

        txtNome = new JTextField();
        txtTelefone = new JTextField();
        txtEmail = new JTextField();
        txtCpf = new JTextField();
        txtData = new JTextField();
        txtData.setEnabled(false);

        p.add(new JLabel("Código:"));
        p.add(txtId);
        p.add(new JLabel("Nome:"));
        p.add(txtNome);
        p.add(new JLabel("Telefone:"));
        p.add(txtTelefone);
        p.add(new JLabel("E-mail:"));
        p.add(txtEmail);
        p.add(new JLabel("CPF:"));
        p.add(txtCpf);
        p.add(new JLabel("Data Cadastro:"));
        p.add(txtData);

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

    // ======================
    // CRUD + Navegação
    // ======================

    private void carregarClientes() {
        try {
            clientes = dao.listar();
            if (!clientes.isEmpty()) {
                posicao = 0;
                mostrar();
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void mostrar() {
        Cliente c = clientes.get(posicao);
        txtId.setText(String.valueOf(c.getIdCliente()));
        txtNome.setText(c.getNome());
        txtTelefone.setText(c.getTelefone());
        txtEmail.setText(c.getEmail());
        txtCpf.setText(c.getCpf());
        txtData.setText(c.getDataCadastro() != null ? c.getDataCadastro().toString() : "");
    }

    private void primeiro() { posicao = 0; mostrar(); }
    private void ultimo() { posicao = clientes.size() - 1; mostrar(); }
    private void anterior() { if (posicao > 0) posicao--; mostrar(); }
    private void proximo() { if (posicao < clientes.size() - 1) posicao++; mostrar(); }

    private void novo() {
        txtId.setText("");
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtCpf.setText("");
        txtData.setText("");
    }

    private void salvar() {
        try {
            Cliente c = new Cliente();
            c.setNome(txtNome.getText());
            c.setTelefone(txtTelefone.getText());
            c.setEmail(txtEmail.getText());
            c.setCpf(txtCpf.getText());

            dao.salvar(c);
            carregarClientes();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void editar() {
        try {
            Cliente c = new Cliente();
            c.setIdCliente(Integer.parseInt(txtId.getText()));
            c.setNome(txtNome.getText());
            c.setTelefone(txtTelefone.getText());
            c.setEmail(txtEmail.getText());
            c.setCpf(txtCpf.getText());

            dao.atualizar(c);
            carregarClientes();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void excluir() {
        try {
            dao.excluir(Integer.parseInt(txtId.getText()));
            carregarClientes();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void erro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
