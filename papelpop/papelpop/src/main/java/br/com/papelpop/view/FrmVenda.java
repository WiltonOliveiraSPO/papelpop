package br.com.papelpop.view;

import br.com.papelpop.dao.*;

import br.com.papelpop.model.*;
import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FrmVenda extends JFrame {
	
	private List<Venda> listaVendas = new ArrayList<>();
	private int indiceAtual = -1;

    private JComboBox<Cliente> cbCliente;
    private JComboBox<Produto> cbProduto;
    private JTextField txtQtd, txtTotal;

    private DefaultTableModel model;
    private List<VendaItem> itens = new ArrayList<>();

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private EstoqueDAO estoqueDAO = new EstoqueDAO();
    private VendaDAO vendaDAO = new VendaDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();


    public FrmVenda() {
        setTitle("Venda");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarTopo(), BorderLayout.NORTH);
        add(criarTabela(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    private JPanel criarTopo() {
        JPanel p = new JPanel(new GridLayout(2, 4, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        cbCliente = new JComboBox<>();
        cbProduto = new JComboBox<>();
        txtQtd = new JTextField();

        carregarCombos();
        
        JPanel painelPrincipal = new JPanel(new BorderLayout());

        // =========================
        // LINHA 1 - CRUD + NAVEGAÇÃO
        // =========================
        JPanel linha1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        linha1.add(botao("➕ Novo", e -> novo()));
        linha1.add(botao("💾 Salvar", e -> finalizar()));
        linha1.add(botao("🗑️ Excluir", e -> excluir()));

        linha1.add(Box.createHorizontalStrut(30));

        linha1.add(botao("⏮", e -> primeiro()));
        linha1.add(botao("◀", e -> anterior()));
        linha1.add(botao("▶", e -> proximo()));
        linha1.add(botao("⏭", e -> ultimo()));

        // =========================
        // LINHA 2 - DADOS DA VENDA
        // =========================
        JPanel linha2 = new JPanel(new GridLayout(2, 4, 10, 10));
        linha2.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        carregarCombos();

        linha2.add(new JLabel("Cliente"));
        linha2.add(cbCliente);
        linha2.add(new JLabel("Produto"));
        linha2.add(cbProduto);
        linha2.add(new JLabel("Quantidade"));
        linha2.add(txtQtd);
        linha2.add(botao("➕ Adicionar", e -> adicionarItem()));

        painelPrincipal.add(linha1, BorderLayout.NORTH);
        painelPrincipal.add(linha2, BorderLayout.SOUTH);

        return painelPrincipal;
    }

    private JScrollPane criarTabela() {
        model = new DefaultTableModel(
                new String[]{"Produto", "Qtd", "Preço", "Subtotal"}, 0);

        JTable table = new JTable(model);
        return new JScrollPane(table);
    }

    private JPanel criarRodape() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(TemaPapelPop.FUNDO_CLARO);

        txtTotal = new JTextField("0.00", 10);
        txtTotal.setEnabled(false);

        p.add(new JLabel("Total R$"));
        p.add(txtTotal);
        p.add(botao("💾 Finalizar Venda", e -> finalizar()));

        return p;
    }

    private RoundedButton botao(String txt, java.awt.event.ActionListener ac) {
        RoundedButton b = new RoundedButton(txt);
        b.addActionListener(ac);
        return b;
    }

    // ======================
    // AÇÕES
    // ======================

    private void adicionarItem() {
        try {
            Produto p = (Produto) cbProduto.getSelectedItem();
            int qtd = Integer.parseInt(txtQtd.getText());

            // valida estoque
            int estoqueAtual = estoqueDAO.buscarQuantidade(p.getIdProduto());

            if (qtd > estoqueAtual) {
                JOptionPane.showMessageDialog(this,
                        "Estoque insuficiente!");
                return;
            }

            double subtotal = qtd * p.getPreco();

            VendaItem item = new VendaItem();
            item.setIdProduto(p.getIdProduto());
            item.setDescricaoProduto(p.getDescricao());
            item.setQuantidade(qtd);
            item.setPrecoUnit(p.getPreco());
            item.setSubtotal(subtotal);

            itens.add(item);

            model.addRow(new Object[]{
                    p.getDescricao(), qtd, p.getPreco(), subtotal
            });

            atualizarTotal();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void atualizarTotal() {
        double total = itens.stream().mapToDouble(VendaItem::getSubtotal).sum();
        txtTotal.setText(String.valueOf(total));
    }

    private void finalizar() {
        try {
            Venda v = new Venda();
            v.setIdCliente(((Cliente) cbCliente.getSelectedItem()).getIdCliente());
            v.setIdUsuario(1); // depois integrar login
            v.setTotal(Double.parseDouble(txtTotal.getText()));
            v.setItens(itens);

            vendaDAO.salvarVenda(v);

            JOptionPane.showMessageDialog(this, "Venda realizada!");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void carregarCombos() {

        try {

            cbCliente.removeAllItems();
            cbProduto.removeAllItems();

            // Clientes
            for (Cliente c : clienteDAO.listar()) {
                cbCliente.addItem(c);
            }

            // Produtos ativos
            for (Produto p : produtoDAO.listar()) {
                if (p.isAtivo() == true) {
                    cbProduto.addItem(p);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    
    
    private void novo() {
        cbCliente.setSelectedIndex(-1);
        cbProduto.setSelectedIndex(-1);
        txtQtd.setText("");
        txtTotal.setText("0.00");

        itens.clear();
        model.setRowCount(0);

        indiceAtual = -1;
    }

    private void excluir() {
        if (indiceAtual >= 0) {
            try {
                Venda v = listaVendas.get(indiceAtual);
                vendaDAO.excluir(v.getIdVenda());
                carregarVendas();
                JOptionPane.showMessageDialog(this, "Venda excluída!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void carregarVendas() {

        try {

            listaVendas = vendaDAO.listar();

            if (!listaVendas.isEmpty()) {
                indiceAtual = 0;
                mostrarVenda(listaVendas.get(indiceAtual));
            } else {
                novo();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    private void primeiro() {
        if (!listaVendas.isEmpty()) {
            indiceAtual = 0;
            mostrarVenda(listaVendas.get(indiceAtual));
        }
    }

    private void anterior() {
        if (indiceAtual > 0) {
            indiceAtual--;
            mostrarVenda(listaVendas.get(indiceAtual));
        }
    }

    private void proximo() {
        if (indiceAtual < listaVendas.size() - 1) {
            indiceAtual++;
            mostrarVenda(listaVendas.get(indiceAtual));
        }
    }

    private void ultimo() {
        if (!listaVendas.isEmpty()) {
            indiceAtual = listaVendas.size() - 1;
            mostrarVenda(listaVendas.get(indiceAtual));
        }
    }

    private void mostrarVenda(Venda v) {

        // Selecionar cliente
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getIdCliente() == v.getIdCliente()) {
                cbCliente.setSelectedIndex(i);
                break;
            }
        }

        // Limpar tabela
        model.setRowCount(0);
        itens.clear();

        // Carregar itens
        for (VendaItem item : v.getItens()) {

            itens.add(item);

            model.addRow(new Object[]{
                    item.getDescricaoProduto(),
                    item.getQuantidade(),
                    item.getPrecoUnit(),
                    item.getSubtotal()
            });
        }

        txtTotal.setText(String.valueOf(v.getTotal()));
    }

}
