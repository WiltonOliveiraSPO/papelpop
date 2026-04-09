package br.com.papelpop.app;

import br.com.papelpop.view.FrmLogin;
import br.com.papelpop.view.FrmMenuPrincipal;

import javax.swing.*;

/**
 * Representa a classe Main e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class Main {

    // Ação: executa a rotina 'main' desta classe.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	new FrmLogin().setVisible(true);
        });
    }
}
