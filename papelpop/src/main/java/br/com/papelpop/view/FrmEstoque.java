package br.com.papelpop.view;

import br.com.papelpop.dao.EstoqueDAO;
import br.com.papelpop.model.Estoque;
import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FrmEstoque extends JFrame {

    private JTextField txtIdProduto, txtDescricao, txtQuantidade, txtMovimento;
    private List<Estoque> estoque;
    private int posicao = 0;

    private EstoqueDAO dao = new EstoqueDAO();

    public FrmEstoque() {
        setTitle("Controle de Estoque");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarBotoes(), BorderLayout.SOUTH);

        carregar();
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        txtIdProduto = new JTextField();
        txtIdProduto.setEnabled(false);

        txtDescricao = new JTextField();
        txtDescricao.setEnabled(false);

        txtQuantidade = new JTextField();
        txtQuantidade.setEnabled(false);

        txtMovimento = new JTextField();

        p.add(new JLabel("Código Produto:"));
        p.add(txtIdProduto);
        p.add(new JLabel("Descrição:"));
        p.add(txtDescricao);
        p.add(new JLabel("Quantidade Atual:"));
        p.add(txtQuantidade);
        p.add(new JLabel("Qtd. Movimento:"));
        p.add(txtMovimento);

        return p;
    }

    private JPanel criarBotoes() {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 10));
        p.setBackground(TemaPapelPop.FUNDO_CLARO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p.add(botao("➕ Entrada", e -> entrada()));
        p.add(botao("➖ Saída", e -> saida()));
        p.add(botao("⏮ Anterior", e -> anterior()));
        p.add(botao("▶ Próximo", e -> proximo()));

        return p;
    }

    private RoundedButton botao(String texto, java.awt.event.ActionListener ac) {
        RoundedButton b = new RoundedButton(texto);
        b.addActionListener(ac);
        return b;
    }

    // =====================
    // AÇÕES
    // =====================

    private void carregar() {
        try {
            estoque = dao.listar();
            if (!estoque.isEmpty()) {
                posicao = 0;
                mostrar();
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void mostrar() {
        Estoque e = estoque.get(posicao);
        txtIdProduto.setText(String.valueOf(e.getIdProduto()));
        txtDescricao.setText(e.getDescricaoProduto());
        txtQuantidade.setText(String.valueOf(e.getQuantidade()));
    }

    private void anterior() {
        if (posicao > 0) posicao--;
        mostrar();
    }

    private void proximo() {
        if (posicao < estoque.size() - 1) posicao++;
        mostrar();
    }

    private void entrada() {
        try {
            dao.entrada(
                Integer.parseInt(txtIdProduto.getText()),
                Integer.parseInt(txtMovimento.getText())
            );
            carregar();
        } catch (Exception e) {
            erro(e);
        }
    }

    private void saida() {
        try {
            dao.saida(
                Integer.parseInt(txtIdProduto.getText()),
                Integer.parseInt(txtMovimento.getText())
            );
            carregar();
        } catch (Exception e) {
            erro(e);
        }
    }

    private void erro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
