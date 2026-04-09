package br.com.papelpop.view;

import br.com.papelpop.dao.*;

import br.com.papelpop.model.*;
import br.com.papelpop.util.IconeSistema;
import br.com.papelpop.util.NotaFiscalPdfService;
import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a classe FrmVenda e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

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
    private NotaFiscalPdfService notaFiscalPdfService = new NotaFiscalPdfService();


    // Ação: executa a rotina 'FrmVenda' desta classe.
    public FrmVenda() {
        setTitle("Venda");
        IconeSistema.aplicarIcone(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarTopo(), BorderLayout.NORTH);
        add(criarTabela(), BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);

        carregarVendas();
        if (listaVendas.isEmpty()) {
            novo();
        }
    }

    // Ação: executa a rotina 'criarTopo' desta classe.
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

    // Ação: executa a rotina 'criarTabela' desta classe.
    private JScrollPane criarTabela() {
        model = new DefaultTableModel(
                new String[]{"Produto", "Qtd", "Preço", "Subtotal"}, 0);

        JTable table = new JTable(model);
        return new JScrollPane(table);
    }

    // Ação: executa a rotina 'criarRodape' desta classe.
    private JPanel criarRodape() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(TemaPapelPop.FUNDO_CLARO);

        txtTotal = new JTextField("0.00", 10);
        txtTotal.setEnabled(false);

        p.add(new JLabel("Total R$"));
        p.add(txtTotal);
        p.add(botao("🧾 Emitir Nota PDF", e -> emitirNotaFiscalPdf()));
        p.add(botao("💾 Finalizar Venda", e -> finalizar()));

        return p;
    }

    // Ação: executa a rotina 'botao' desta classe.
    private RoundedButton botao(String txt, java.awt.event.ActionListener ac) {
        RoundedButton b = new RoundedButton(txt);
        b.addActionListener(ac);
        return b;
    }

    // ======================
    // AÇÕES
    // ======================

    // Ação: executa a rotina 'adicionarItem' desta classe.
    private void adicionarItem() {
        try {
            Produto p = (Produto) cbProduto.getSelectedItem();
            if (p == null) {
                throw new IllegalArgumentException("Selecione um produto.");
            }

            String quantidadeTexto = txtQtd.getText() == null ? "" : txtQtd.getText().trim();
            if (quantidadeTexto.isEmpty()) {
                throw new IllegalArgumentException("Informe a quantidade.");
            }

            int qtd = Integer.parseInt(quantidadeTexto);
            if (qtd <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }

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
            txtQtd.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade invalida. Digite apenas numeros.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Ação: executa a rotina 'atualizarTotal' desta classe.
    private void atualizarTotal() {
        double total = itens.stream().mapToDouble(VendaItem::getSubtotal).sum();
        txtTotal.setText(String.valueOf(total));
    }

    // Ação: executa a rotina 'finalizar' desta classe.
    private void finalizar() {
        try {
            Venda v = montarVendaAtual();
            validarVenda(v);

            int idVendaGerada = vendaDAO.salvarVendaRetornandoId(v);
            carregarVendas();
            selecionarVendaPorId(idVendaGerada);
            JOptionPane.showMessageDialog(this, "Venda finalizada com sucesso!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Ação: executa a rotina 'emitirNotaFiscalPdf' desta classe.
    private void emitirNotaFiscalPdf() {
        try {
            if (indiceAtual >= 0 && indiceAtual < listaVendas.size()) {
                Venda vendaSelecionada = listaVendas.get(indiceAtual);
                String nomeCliente = buscarNomeCliente(vendaSelecionada.getIdCliente());
                Path caminhoPdf = notaFiscalPdfService.gerarAbrirNota(
                        vendaSelecionada.getIdVenda(), nomeCliente, vendaSelecionada);

                JOptionPane.showMessageDialog(this,
                        "NF emitida para a venda #" + vendaSelecionada.getIdVenda()
                                + "\nArquivo: " + caminhoPdf);
                return;
            }

            Venda novaVenda = montarVendaAtual();
            validarVenda(novaVenda);
            int idVendaGerada = vendaDAO.salvarVendaRetornandoId(novaVenda);
            carregarVendas();
            selecionarVendaPorId(idVendaGerada);

            String nomeCliente = ((Cliente) cbCliente.getSelectedItem()).toString();
            Path caminhoPdf = notaFiscalPdfService.gerarAbrirNota(idVendaGerada, nomeCliente, novaVenda);
            JOptionPane.showMessageDialog(this,
                    "NF emitida com sucesso para a nova venda!\nArquivo: " + caminhoPdf);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // Ação: executa a rotina 'montarVendaAtual' desta classe.
    private Venda montarVendaAtual() {
        Venda v = new Venda();
        Cliente cliente = (Cliente) cbCliente.getSelectedItem();
        if (cliente != null) {
            v.setIdCliente(cliente.getIdCliente());
        }
        v.setIdUsuario(1); // depois integrar login
        String totalTexto = txtTotal.getText() == null ? "" : txtTotal.getText().trim();
        if (totalTexto.isEmpty()) {
            totalTexto = "0";
        }
        totalTexto = totalTexto.replace(",", ".");
        v.setTotal(Double.parseDouble(totalTexto));
        v.setItens(new ArrayList<>(itens));
        return v;
    }

    // Ação: executa a rotina 'validarVenda' desta classe.
    private void validarVenda(Venda v) {
        if (cbCliente.getSelectedItem() == null) {
            throw new IllegalArgumentException("Selecione um cliente para a venda.");
        }
        if (v.getItens() == null || v.getItens().isEmpty()) {
            throw new IllegalArgumentException("Adicione ao menos um item na venda.");
        }
        if (v.getTotal() <= 0d) {
            throw new IllegalArgumentException("Total da venda invalido.");
        }
    }

    // Ação: executa a rotina 'carregarCombos' desta classe.
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

    
    
    // Ação: executa a rotina 'novo' desta classe.
    private void novo() {
        cbCliente.setSelectedIndex(-1);
        cbProduto.setSelectedIndex(-1);
        txtQtd.setText("");
        txtTotal.setText("0.00");

        itens.clear();
        model.setRowCount(0);

        indiceAtual = -1;
    }

    // Ação: executa a rotina 'excluir' desta classe.
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

    // Ação: executa a rotina 'carregarVendas' desta classe.
    private void carregarVendas() {

        try {

            listaVendas = vendaDAO.listar();

            if (!listaVendas.isEmpty()) {
                if (indiceAtual < 0 || indiceAtual >= listaVendas.size()) {
                    indiceAtual = 0;
                }
                mostrarVenda(listaVendas.get(indiceAtual));
            } else {
                novo();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    // Ação: executa a rotina 'primeiro' desta classe.
    private void primeiro() {
        if (!listaVendas.isEmpty()) {
            indiceAtual = 0;
            mostrarVenda(listaVendas.get(indiceAtual));
        }
    }

    // Ação: executa a rotina 'anterior' desta classe.
    private void anterior() {
        if (indiceAtual > 0) {
            indiceAtual--;
            mostrarVenda(listaVendas.get(indiceAtual));
        }
    }

    // Ação: executa a rotina 'proximo' desta classe.
    private void proximo() {
        if (indiceAtual < listaVendas.size() - 1) {
            indiceAtual++;
            mostrarVenda(listaVendas.get(indiceAtual));
        }
    }

    // Ação: executa a rotina 'ultimo' desta classe.
    private void ultimo() {
        if (!listaVendas.isEmpty()) {
            indiceAtual = listaVendas.size() - 1;
            mostrarVenda(listaVendas.get(indiceAtual));
        }
    }

    // Ação: executa a rotina 'mostrarVenda' desta classe.
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

    // Ação: executa a rotina 'selecionarVendaPorId' desta classe.
    private void selecionarVendaPorId(int idVenda) {
        for (int i = 0; i < listaVendas.size(); i++) {
            if (listaVendas.get(i).getIdVenda() == idVenda) {
                indiceAtual = i;
                mostrarVenda(listaVendas.get(indiceAtual));
                return;
            }
        }
    }

    // Ação: executa a rotina 'buscarNomeCliente' desta classe.
    private String buscarNomeCliente(int idCliente) {
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            Cliente cliente = cbCliente.getItemAt(i);
            if (cliente.getIdCliente() == idCliente) {
                return cliente.toString();
            }
        }
        return "Cliente nao identificado";
    }

}
