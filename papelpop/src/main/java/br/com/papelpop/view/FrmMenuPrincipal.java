package br.com.papelpop.view;

import br.com.papelpop.util.TemaPapelPop;
import br.com.papelpop.view.components.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class FrmMenuPrincipal extends JFrame {

    private FrmUsuario frmUsuario; // referência da tela
    private FrmCliente frmCliente; // referência da tela
    private FrmProduto frmProduto; // referência da tela
    private FrmVenda frmVenda; // referência da tela


    public FrmMenuPrincipal() {
        setTitle("PapelPop - Sistema de Papelaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarBarraSuperior(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
    }

    private JPanel criarBarraSuperior() {
        JPanel panel = new JPanel(new GridLayout(1, 7, 10, 0));
        panel.setBackground(TemaPapelPop.FUNDO_CLARO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(criarBotao("👥 Clientes", e -> abrirCliente()));
        panel.add(criarBotao("🛒 Produtos", e -> abrirProduto()));
        panel.add(criarBotao("💰 Vendas", e -> abrirVendas()));
        panel.add(criarBotao("🔍 Consultas", null));
        panel.add(criarBotao("📊 Relatórios", null));
        panel.add(criarBotao("👤 Usuários", e -> abrirUsuarios()));
        panel.add(criarBotao("🚪 Sair", e -> sair()));

        return panel;
    }

    private void abrirVendas() {
    	if (frmVenda == null || !frmVenda.isDisplayable()) {
    		frmVenda = new FrmVenda();
    		frmVenda.setVisible(true);
        } else {
        	frmVenda.toFront();
        	frmVenda.requestFocus();
        }
	}

	private void abrirProduto() {
    	if (frmProduto == null || !frmProduto.isDisplayable()) {
    		frmProduto = new FrmProduto();
    		frmProduto.setVisible(true);
        } else {
        	frmProduto.toFront();
        	frmProduto.requestFocus();
        }
	}

	private void abrirCliente() {
    	if (frmCliente == null || !frmCliente.isDisplayable()) {
    		frmCliente = new FrmCliente();
    		frmCliente.setVisible(true);
        } else {
        	frmCliente.toFront();
        	frmCliente.requestFocus();
        }
    }


	private void sair() {
    int opcao = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente sair do sistema?",
            "Confirmação",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
    );

    if (opcao == JOptionPane.YES_OPTION) {
        dispose();      // fecha o menu
        System.exit(0); // encerra a aplicação
    }
}


	private RoundedButton criarBotao(String texto, java.awt.event.ActionListener acao) {
        RoundedButton btn = new RoundedButton(texto);
        btn.setPreferredSize(new Dimension(150, 65));

        if (acao != null) {
            btn.addActionListener(acao);
        }

        return btn;
    }

    private JPanel criarPainelCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        ImageIcon icon = new ImageIcon("C:/papelpop/icons/papelpop.jpg");
        Image img = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(img));

        panel.add(lblLogo);
        return panel;
    }

    // ======================
    // AÇÕES
    // ======================

    private void abrirUsuarios() {
        if (frmUsuario == null || !frmUsuario.isDisplayable()) {
            frmUsuario = new FrmUsuario();
            frmUsuario.setVisible(true);
        } else {
            frmUsuario.toFront();
            frmUsuario.requestFocus();
        }
    }
}
