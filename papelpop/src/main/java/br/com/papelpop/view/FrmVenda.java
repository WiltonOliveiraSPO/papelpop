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

    private JComboBox<Cliente> cbCliente;
    private JComboBox<Produto> cbProduto;
    private JTextField txtQtd, txtTotal;

    private DefaultTableModel model;
    private List<VendaItem> itens = new ArrayList<>();

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private EstoqueDAO estoqueDAO = new EstoqueDAO();
    private VendaDAO vendaDAO = new VendaDAO();

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

        p.add(new JLabel("Cliente"));
        p.add(cbCliente);
        p.add(new JLabel("Produto"));
        p.add(cbProduto);
        p.add(new JLabel("Quantidade"));
        p.add(txtQtd);

        p.add(botao("➕ Adicionar", e -> adicionarItem()));

        return p;
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
            int estoqueAtual = estoqueDAO
                .listar()
                .stream()
                .filter(e -> e.getIdProduto() == p.getIdProduto())
                .findFirst()
                .get()
                .getQuantidade();

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
        // carregar clientes e produtos ativos
        // segue o mesmo padrão dos outros DAOs
    }
}
