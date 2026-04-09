package br.com.papelpop.view;

import br.com.papelpop.util.TemaPapelPop;

import br.com.papelpop.view.components.RoundedButton;
import br.com.papelpop.util.IconeSistema;
import javax.swing.*;
import java.awt.*;

/**
 * Representa a classe FrmMenuPrincipal e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class FrmMenuPrincipal extends JFrame {

    private FrmUsuario frmUsuario; // referência da tela
    private FrmCliente frmCliente; // referência da tela
    private FrmProduto frmProduto; // referência da tela
    private FrmVenda frmVenda; // referência da tela
    private FrmEstoque frmEstoque; // referência da tela


    // Ação: executa a rotina 'FrmMenuPrincipal' desta classe.
    public FrmMenuPrincipal() {
        setTitle("PapelPop - Sistema de Papelaria");

        IconeSistema.aplicarIcone(this); // 👈 AQUI

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(criarBarraSuperior(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
    }

    // Ação: executa a rotina 'criarBarraSuperior' desta classe.
    private JPanel criarBarraSuperior() {
        JPanel panel = new JPanel(new GridLayout(1, 8, 10, 0));
        panel.setBackground(TemaPapelPop.FUNDO_CLARO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(criarBotao("👥 Clientes", e -> abrirCliente()));
        panel.add(criarBotao("🛒 Produtos", e -> abrirProduto()));
        panel.add(criarBotao("📦 Estoque", e -> abrirEstoque()));
        panel.add(criarBotao("💰 Vendas", e -> abrirVendas()));
        panel.add(criarBotao("🔍 Consultas", null));
        panel.add(criarBotao("📊 Relatórios", e -> abrirRelatorios()));
        panel.add(criarBotao("👤 Usuários", e -> abrirUsuarios()));
        panel.add(criarBotao("🚪 Sair", e -> sair()));

        return panel;
    }

    // Ação: executa a rotina 'abrirVendas' desta classe.
    private void abrirVendas() {
    	if (frmVenda == null || !frmVenda.isDisplayable()) {
    		frmVenda = new FrmVenda();
    		frmVenda.setVisible(true);
        } else {
            if ((frmVenda.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                frmVenda.setExtendedState(Frame.NORMAL);
            }
            if (!frmVenda.isVisible()) {
                frmVenda.setVisible(true);
            }
        	frmVenda.toFront();
        	frmVenda.requestFocus();
        }
	}

	// Ação: executa a rotina 'abrirProduto' desta classe.
	private void abrirProduto() {
    	if (frmProduto == null || !frmProduto.isDisplayable()) {
    		frmProduto = new FrmProduto();
    		frmProduto.setVisible(true);
        } else {
            if ((frmProduto.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                frmProduto.setExtendedState(Frame.NORMAL);
            }
            if (!frmProduto.isVisible()) {
                frmProduto.setVisible(true);
            }
        	frmProduto.toFront();
        	frmProduto.requestFocus();
        }
	}
	
	// Ação: executa a rotina 'abrirEstoque' desta classe.
	private void abrirEstoque() {
	    if (frmEstoque == null || !frmEstoque.isDisplayable()) {
	        frmEstoque = new FrmEstoque();
	        frmEstoque.setVisible(true);
	    } else {
	        if ((frmEstoque.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
	            frmEstoque.setExtendedState(Frame.NORMAL);
	        }
            if (!frmEstoque.isVisible()) {
                frmEstoque.setVisible(true);
            }
	        frmEstoque.toFront();
	        frmEstoque.requestFocus();
	    }
	}


	// Ação: executa a rotina 'abrirCliente' desta classe.
	private void abrirCliente() {
    	if (frmCliente == null || !frmCliente.isDisplayable()) {
    		frmCliente = new FrmCliente();
    		frmCliente.setVisible(true);
        } else {
            if ((frmCliente.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                frmCliente.setExtendedState(Frame.NORMAL);
            }
            if (!frmCliente.isVisible()) {
                frmCliente.setVisible(true);
            }
        	frmCliente.toFront();
        	frmCliente.requestFocus();
        }
    }
	
	private FrmRelatorioVendas frmRelatorio;

	// Ação: executa a rotina 'abrirRelatorios' desta classe.
	private void abrirRelatorios() {
	    if (frmRelatorio == null || !frmRelatorio.isDisplayable()) {
	        frmRelatorio = new FrmRelatorioVendas();
	        frmRelatorio.setVisible(true);
	    } else {
            if ((frmRelatorio.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                frmRelatorio.setExtendedState(Frame.NORMAL);
            }
            if (!frmRelatorio.isVisible()) {
                frmRelatorio.setVisible(true);
            }
	        frmRelatorio.toFront();
            frmRelatorio.requestFocus();
	    }
	}



	// Ação: executa a rotina 'sair' desta classe.
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


	// Ação: executa a rotina 'criarBotao' desta classe.
	private RoundedButton criarBotao(String texto, java.awt.event.ActionListener acao) {
        RoundedButton btn = new RoundedButton(texto);
        btn.setPreferredSize(new Dimension(150, 65));

        if (acao != null) {
            btn.addActionListener(acao);
        }

        return btn;
    }

    // Ação: executa a rotina 'criarPainelCentral' desta classe.
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

    // Ação: executa a rotina 'abrirUsuarios' desta classe.
    private void abrirUsuarios() {
        if (frmUsuario == null || !frmUsuario.isDisplayable()) {
            frmUsuario = new FrmUsuario();
            frmUsuario.setVisible(true);
        } else {
            if ((frmUsuario.getExtendedState() & Frame.ICONIFIED) == Frame.ICONIFIED) {
                frmUsuario.setExtendedState(Frame.NORMAL);
            }
            if (!frmUsuario.isVisible()) {
                frmUsuario.setVisible(true);
            }
            frmUsuario.toFront();
            frmUsuario.requestFocus();
        }
    }
}
