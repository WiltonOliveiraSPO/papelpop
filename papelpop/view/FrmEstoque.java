package br.com.papelpop.view;

import br.com.papelpop.dao.EstoqueDAO;
import br.com.papelpop.dao.ProdutoDAO;
import br.com.papelpop.model.Estoque;
import br.com.papelpop.model.Produto;
import br.com.papelpop.util.IconeSistema;
import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Representa a classe FrmEstoque e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class FrmEstoque extends JFrame {

    private JComboBox<Produto> cbProduto;
    private JTextField txtQuantidade, txtMovimento;

    private List<Estoque> lista;
    private int posicao = 0;

    private EstoqueDAO estoqueDAO = new EstoqueDAO();
    private ProdutoDAO produtoDAO = new ProdutoDAO();

    // Ação: executa a rotina 'FrmEstoque' desta classe.
    public FrmEstoque() {
        setTitle("Controle de Estoque");
        IconeSistema.aplicarIcone(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarBarraBotoes(), BorderLayout.SOUTH);

        carregar();
    }

    // Ação: executa a rotina 'criarFormulario' desta classe.
    private JPanel criarFormulario() {
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cbProduto = new JComboBox<>();
        txtQuantidade = new JTextField();
        txtQuantidade.setEnabled(false);

        txtMovimento = new JTextField();

        carregarProdutos();
        cbProduto.addActionListener(e -> atualizarQuantidadeSelecionada());

        p.add(new JLabel("Produto:"));
        p.add(cbProduto);

        p.add(new JLabel("Quantidade Atual:"));
        p.add(txtQuantidade);

        p.add(new JLabel("Qtd. Movimento:"));
        p.add(txtMovimento);

        return p;
    }

    // Ação: executa a rotina 'criarBarraBotoes' desta classe.
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

    // Ação: executa a rotina 'botao' desta classe.
    private RoundedButton botao(String texto, java.awt.event.ActionListener ac) {
        RoundedButton b = new RoundedButton(texto);
        b.addActionListener(ac);
        return b;
    }

    // ======================
    // CARREGAMENTOS
    // ======================

    // Ação: executa a rotina 'carregarProdutos' desta classe.
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

    // Ação: executa a rotina 'carregar' desta classe.
    private void carregar() {
        try {
            lista = estoqueDAO.listar();
            if (!lista.isEmpty()) {
                posicao = 0;
                mostrar();
            } else {
                txtQuantidade.setText("0");
            }
        } catch (SQLException e) {
            erro(e);
        }
    }

    // Ação: executa a rotina 'mostrar' desta classe.
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

    // Ação: executa a rotina 'atualizarQuantidadeSelecionada' desta classe.
    private void atualizarQuantidadeSelecionada() {
        Produto p = (Produto) cbProduto.getSelectedItem();
        if (p == null) {
            txtQuantidade.setText("0");
            return;
        }

        try {
            int qtd = estoqueDAO.buscarQuantidade(p.getIdProduto());
            txtQuantidade.setText(String.valueOf(qtd));
        } catch (SQLException e) {
            erro(e);
        }
    }

    // ======================
    // NAVEGAÇÃO
    // ======================

    private void primeiro() { posicao = 0; mostrar(); }
    private void ultimo() { posicao = lista.size() - 1; mostrar(); }

    // Ação: executa a rotina 'anterior' desta classe.
    private void anterior() {
        if (posicao > 0) posicao--;
        mostrar();
    }

    // Ação: executa a rotina 'proximo' desta classe.
    private void proximo() {
        if (posicao < lista.size() - 1) posicao++;
        mostrar();
    }
    
 // ======================
 // NOVO / SALVAR
 // ======================

 // Ação: executa a rotina 'novo' desta classe.
 private void novo() {
     cbProduto.setSelectedIndex(-1);
     txtQuantidade.setText("0");
     txtMovimento.setText("");
 }

 // Ação: executa a rotina 'salvarNovoRegistro' desta classe.
 private void salvarNovoRegistro() {
     try {
         Produto p = (Produto) cbProduto.getSelectedItem();

         if (p == null) {
             JOptionPane.showMessageDialog(this, "Selecione um produto!");
             return;
         }

         estoqueDAO.criarEstoqueSeNaoExistir(p.getIdProduto());
         int qtdMovimento = 0;
         String textoMovimento = txtMovimento.getText().trim();
         if (!textoMovimento.isEmpty()) {
             qtdMovimento = Integer.parseInt(textoMovimento);
             if (qtdMovimento < 0) {
                 JOptionPane.showMessageDialog(this, "Quantidade nao pode ser negativa!");
                 return;
             }
         }

         if (qtdMovimento > 0) {
             estoqueDAO.entrada(p.getIdProduto(), qtdMovimento);
         }

         JOptionPane.showMessageDialog(this,
                 "Registro de estoque criado com sucesso!"
                 + (qtdMovimento > 0 ? " Quantidade inicial adicionada: " + qtdMovimento : ""));
         carregar();
         atualizarQuantidadeSelecionada();
         txtMovimento.setText("");

     } catch (NumberFormatException e) {
         JOptionPane.showMessageDialog(this, "Digite apenas numeros na quantidade!");
     } catch (Exception e) {
         erro(e);
     }
 }


    // ======================
    // MOVIMENTAÇÃO
    // ======================

 // Ação: executa a rotina 'entrada' desta classe.
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
            atualizarQuantidadeSelecionada();
	        txtMovimento.setText("");

	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(this, "Digite apenas números!");
	    } catch (Exception e) {
	        erro(e);
	    }
	}

 // Ação: executa a rotina 'saida' desta classe.
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
            atualizarQuantidadeSelecionada();
	        txtMovimento.setText("");

	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(this, "Digite apenas números!");
	    } catch (Exception e) {
	        erro(e);
	    }
	}

    // Ação: executa a rotina 'erro' desta classe.
    private void erro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
