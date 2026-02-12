package br.com.papelpop.view;

import br.com.papelpop.dao.EstoqueDAO;
import br.com.papelpop.dao.ProdutoDAO;
import br.com.papelpop.model.Estoque;
import br.com.papelpop.model.Produto;
import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FrmEstoque extends JFrame {

    private JComboBox<Produto> cbProduto;
    private JTextField txtQuantidade, txtMovimento;

    private List<Estoque> lista;
    private int posicao = 0;

    private EstoqueDAO estoqueDAO = new EstoqueDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    public FrmEstoque() {
        setTitle("Controle de Estoque");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarBarraBotoes(), BorderLayout.SOUTH);

        carregar();
    }

    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cbProduto = new JComboBox<>();
        txtQuantidade = new JTextField();
        txtQuantidade.setEnabled(false);

        txtMovimento = new JTextField();

        carregarProdutos();

        p.add(new JLabel("Produto:"));
        p.add(cbProduto);

        p.add(new JLabel("Quantidade Atual:"));
        p.add(txtQuantidade);

        p.add(new JLabel("Qtd. Movimento:"));
        p.add(txtMovimento);

        return p;
    }

    private JPanel criarBarraBotoes() {
        JPanel p = new JPanel(new GridLayout(2, 4, 10, 10));
        p.setBackground(TemaPapelPop.FUNDO_CLARO);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Navegação
        p.add(botao("⏮ Primeiro", e -> primeiro()));
        p.add(botao("◀ Anterior", e -> anterior()));
        p.add(botao("▶ Próximo", e -> proximo()));
        p.add(botao("⏭ Último", e -> ultimo()));

        // CRUD + Movimentação
        p.add(botao("➕ Novo", e -> novo()));
        p.add(botao("💾 Criar Registro", e -> salvarNovoRegistro()));
        p.add(botao("➕ Entrada", e -> entrada()));
        p.add(botao("➖ Saída", e -> saida()));

        return p;
    }

    private RoundedButton botao(String texto, java.awt.event.ActionListener ac) {
        RoundedButton b = new RoundedButton(texto);
        b.addActionListener(ac);
        return b;
    }

    // ======================
    // CARREGAMENTOS
    // ======================

    private void carregarProdutos() {
        try {
            cbProduto.removeAllItems();
            for (Produto p : produtoDAO.listar()) {
                if (p.isAtivo()) {
                    cbProduto.addItem(p);
                }
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void carregar() {
        try {
            lista = estoqueDAO.listar();
            if (!lista.isEmpty()) {
                posicao = 0;
                mostrar();
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    private void mostrar() {
        if (lista.isEmpty()) return;

        Estoque e = lista.get(posicao);

        for (int i = 0; i < cbProduto.getItemCount(); i++) {
            Produto p = cbProduto.getItemAt(i);
            if (p.getIdProduto() == e.getIdProduto()) {
                cbProduto.setSelectedIndex(i);
                break;
            }
        }

        txtQuantidade.setText(String.valueOf(e.getQuantidade()));
    }

    // ======================
    // NAVEGAÇÃO
    // ======================

    private void primeiro() { posicao = 0; mostrar(); }
    private void ultimo() { posicao = lista.size() - 1; mostrar(); }

    private void anterior() {
        if (posicao > 0) posicao--;
        mostrar();
    }

    private void proximo() {
        if (posicao < lista.size() - 1) posicao++;
        mostrar();
    }
    
 // ======================
 // NOVO / SALVAR
 // ======================

 private void novo() {
     cbProduto.setSelectedIndex(-1);
     txtQuantidade.setText("0");
     txtMovimento.setText("");
 }

 private void salvarNovoRegistro() {
     try {
         Produto p = (Produto) cbProduto.getSelectedItem();

         if (p == null) {
             JOptionPane.showMessageDialog(this, "Selecione um produto!");
             return;
         }

         estoqueDAO.criarEstoqueSeNaoExistir(p.getIdProduto());

         JOptionPane.showMessageDialog(this, "Registro de estoque criado com sucesso!");
         carregar();

     } catch (Exception e) {
         erro(e);
     }
 }


    // ======================
    // MOVIMENTAÇÃO
    // ======================

 private void entrada() {
	    try {
	        Produto p = (Produto) cbProduto.getSelectedItem();

	        if (p == null) {
	            JOptionPane.showMessageDialog(this, "Selecione um produto!");
	            return;
	        }

	        if (txtMovimento.getText().trim().isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Informe a quantidade!");
	            return;
	        }

	        int qtd = Integer.parseInt(txtMovimento.getText());

	        if (qtd <= 0) {
	            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero!");
	            return;
	        }

	        estoqueDAO.criarEstoqueSeNaoExistir(p.getIdProduto());
	        estoqueDAO.entrada(p.getIdProduto(), qtd);

	        carregar();
	        txtMovimento.setText("");

	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(this, "Digite apenas números!");
	    } catch (Exception e) {
	        erro(e);
	    }
	}

 private void saida() {
	    try {
	        Produto p = (Produto) cbProduto.getSelectedItem();

	        if (p == null) {
	            JOptionPane.showMessageDialog(this, "Selecione um produto!");
	            return;
	        }

	        if (txtMovimento.getText().trim().isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Informe a quantidade!");
	            return;
	        }

	        int qtd = Integer.parseInt(txtMovimento.getText());

	        if (qtd <= 0) {
	            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero!");
	            return;
	        }

	        estoqueDAO.saida(p.getIdProduto(), qtd);

	        carregar();
	        txtMovimento.setText("");

	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(this, "Digite apenas números!");
	    } catch (Exception e) {
	        erro(e);
	    }
	}

    private void erro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
