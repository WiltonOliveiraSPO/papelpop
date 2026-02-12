package br.com.papelpop.view;

import br.com.papelpop.util.ConexaoSQLite;
import br.com.papelpop.util.TemaPapelPop;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FrmRelatorioVendas extends JFrame {

    private JTable tabelaDiario;
    private JTable tabelaPeriodo;

    private JDateChooser dcData;
    private JDateChooser dcInicio;
    private JDateChooser dcFim;
    
    private JLabel lblTotalDiario;
    private JLabel lblTotalPeriodo;


    public FrmRelatorioVendas() {
        setTitle("Relatórios de Vendas");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("📅 Diário", criarAbaDiaria());
        abas.addTab("📆 Por Período", criarAbaPeriodo());

        add(abas, BorderLayout.CENTER);
    }

    // ==============================
    // ABA DIÁRIA
    // ==============================

    private JPanel criarAbaDiaria() {

        JPanel painel = new JPanel(new BorderLayout());

        JPanel topo = new JPanel();
        topo.setBackground(TemaPapelPop.FUNDO_CLARO);

        dcData = new JDateChooser();
        dcData.setDate(new Date());

        JButton btnPesquisar = new JButton(redimensionarIcone("C:/papelpop/icons/lupa.jpg"));
        JButton btnExcel = new JButton(redimensionarIcone("C:/papelpop/icons/excel.jpg"));

        btnPesquisar.addActionListener(e -> pesquisarDiario());
        btnExcel.addActionListener(e -> exportarExcel(tabelaDiario));

        topo.add(new JLabel("Data:"));
        topo.add(dcData);
        topo.add(btnPesquisar);
        topo.add(btnExcel);

        tabelaDiario = new JTable();

        lblTotalDiario = new JLabel("Total: R$ 0,00");
        lblTotalDiario.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(lblTotalDiario);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaDiario), BorderLayout.CENTER);
        painel.add(rodape, BorderLayout.SOUTH);
        
        return painel;
    }

    // ==============================
    // ABA PERÍODO
    // ==============================

    private JPanel criarAbaPeriodo() {

        JPanel painel = new JPanel(new BorderLayout());

        JPanel topo = new JPanel();
        topo.setBackground(TemaPapelPop.FUNDO_CLARO);

        dcInicio = new JDateChooser();
        dcFim = new JDateChooser();

        JButton btnPesquisar = new JButton(redimensionarIcone("C:/papelpop/icons/lupa.jpg"));
        JButton btnExcel = new JButton(redimensionarIcone("C:/papelpop/icons/excel.jpg"));

        btnPesquisar.addActionListener(e -> pesquisarPeriodo());
        btnExcel.addActionListener(e -> exportarExcel(tabelaPeriodo));

        topo.add(new JLabel("De:"));
        topo.add(dcInicio);
        topo.add(new JLabel("Até:"));
        topo.add(dcFim);
        topo.add(btnPesquisar);
        topo.add(btnExcel);

        tabelaPeriodo = new JTable();

        lblTotalPeriodo = new JLabel("Total: R$ 0,00");
        lblTotalPeriodo.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(lblTotalPeriodo);

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaPeriodo), BorderLayout.CENTER);
        painel.add(rodape, BorderLayout.SOUTH);

        return painel;
    }

    // ==============================
    // CONSULTAS
    // ==============================

    private void pesquisarDiario() {

        Date data = dcData.getDate();
        if (data == null) return;

        String dataFormatada = new SimpleDateFormat("yyyy-MM-dd").format(data);

        String sql = """
            SELECT v.id_venda, c.nome, v.total, v.data_venda
              FROM vendas v
              LEFT JOIN clientes c ON c.id_cliente = v.id_cliente
             WHERE date(v.data_venda) = ?
        """;

        carregarTabela(tabelaDiario, sql, dataFormatada);
    }

    private void pesquisarPeriodo() {

        Date inicio = dcInicio.getDate();
        Date fim = dcFim.getDate();
        if (inicio == null || fim == null) return;

        String dataInicio = new SimpleDateFormat("yyyy-MM-dd").format(inicio);
        String dataFim = new SimpleDateFormat("yyyy-MM-dd").format(fim);

        String sql = """
            SELECT v.id_venda, c.nome, v.total, v.data_venda
              FROM vendas v
              LEFT JOIN clientes c ON c.id_cliente = v.id_cliente
             WHERE date(v.data_venda) BETWEEN ? AND ?
        """;

        carregarTabela(tabelaPeriodo, sql, dataInicio, dataFim);
    }

    private void carregarTabela(JTable tabela, String sql, String... params) {

        try (Connection con = ConexaoSQLite.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }

            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{"Venda", "Cliente", "Total", "Data"});

            double totalGeral = 0;

            while (rs.next()) {

                double total = rs.getDouble("total");
                totalGeral += total;

                model.addRow(new Object[]{
                        rs.getInt("id_venda"),
                        rs.getString("nome"),
                        total,
                        rs.getString("data_venda")
                });
            }

            tabela.setModel(model);

            String totalFormatado = String.format("R$ %.2f", totalGeral);

            if (tabela == tabelaDiario) {
                lblTotalDiario.setText("Total: " + totalFormatado);
            } else {
                lblTotalPeriodo.setText("Total: " + totalFormatado);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ==============================
    // EXPORTAR EXCEL
    // ==============================

    private void exportarExcel(JTable tabela) {

        try (Workbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("Relatorio");

            for (int i = 0; i < tabela.getRowCount(); i++) {
                Row row = sheet.createRow(i);

                for (int j = 0; j < tabela.getColumnCount(); j++) {
                    row.createCell(j).setCellValue(
                            tabela.getValueAt(i, j).toString()
                    );
                }
            }

            FileOutputStream out = new FileOutputStream("RelatorioVendas.xlsx");
            wb.write(out);
            out.close();

            JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    private ImageIcon redimensionarIcone(String caminho) {

        ImageIcon icon = new ImageIcon(caminho);
        Image img = icon.getImage();

        int largura = (int) (icon.getIconWidth() * 0.2);
        int altura = (int) (icon.getIconHeight() * 0.2);

        Image imgRedimensionada = img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);

        return new ImageIcon(imgRedimensionada);
    }

}
