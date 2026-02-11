package br.com.papelpop.app;

import br.com.papelpop.view.FrmMenuPrincipal;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FrmMenuPrincipal().setVisible(true);
        });
    }
}
