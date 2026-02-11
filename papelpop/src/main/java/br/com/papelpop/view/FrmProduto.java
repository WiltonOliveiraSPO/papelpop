package br.com.papelpop.view;

import br.com.papelpop.dao.ProdutoDAO;
import br.com.papelpop.model.Produto;
import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FrmProduto extends JFrame {

    private JTextField txtId, txtDescricao, txtPreco;
    private JCheckBox chkAtivo;

    private List<Produto> produtos;
    private int posicao = 0;

    private ProdutoDAO dao = new ProdutoDAO();

    public FrmProduto() {
        setTitle("Cadastro de Produtos");
        setSize(600, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarBarraBotoes(), BorderLayout.SOUTH);

        carregarProdutos();
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtId = new JTextField();
        txtId.setEnabled(false);

        txtDescricao = new JTextField();
        txtPreco = new JTextField();

        chkAtivo = new JCheckBox("Produto Ativo");
        chkAtivo.setSelected(true);

        p.add(new JLabel("Código:"));
        p.add(txtId);
        p.add(new JLabel("Descrição:"));
        p.add(txtDescricao);
        p.add(new JLabel("Preço:"));
        p.add(txtPreco);
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

    // ======================
    // CRUD + Navegação
    // ======================

    private void carregarProdutos() {
        try {
            produtos = dao.listar();
            if (!produtos.isEmpty()) {
                posicao = 0;
                mostrar();
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void mostrar() {
        Produto p = produtos.get(posicao);
        txtId.setText(String.valueOf(p.getIdProduto()));
        txtDescricao.setText(p.getDescricao());
        txtPreco.setText(String.valueOf(p.getPreco()));
        chkAtivo.setSelected(p.isAtivo());
    }

    private void primeiro() { posicao = 0; mostrar(); }
    private void ultimo() { posicao = produtos.size() - 1; mostrar(); }
    private void anterior() { if (posicao > 0) posicao--; mostrar(); }
    private void proximo() { if (posicao < produtos.size() - 1) posicao++; mostrar(); }

    private void novo() {
        txtId.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        chkAtivo.setSelected(true);
    }

    private void salvar() {
        try {
            Produto p = new Produto();
            p.setDescricao(txtDescricao.getText());
            p.setPreco(Double.parseDouble(txtPreco.getText()));
            p.setAtivo(chkAtivo.isSelected());

            dao.salvar(p);
            carregarProdutos();
        } catch (Exception e) {
            erro(e);
        }
    }

    private void editar() {
        try {
            Produto p = new Produto();
            p.setIdProduto(Integer.parseInt(txtId.getText()));
            p.setDescricao(txtDescricao.getText());
            p.setPreco(Double.parseDouble(txtPreco.getText()));
            p.setAtivo(chkAtivo.isSelected());

            dao.atualizar(p);
            carregarProdutos();
        } catch (Exception e) {
            erro(e);
        }
    }

    private void excluir() {
        try {
            dao.excluir(Integer.parseInt(txtId.getText()));
            carregarProdutos();
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void erro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
