package br.com.papelpop.view.components;

import br.com.papelpop.util.TemaPapelPop;

import javax.swing.*;
import java.awt.*;

/**
 * Representa a classe RoundedButton e centraliza suas responsabilidades no sistema PapelPop.
 * Data de criacao: 09/04/2026
 * Autor: Wilton Almeida Oliveira
 */

public class RoundedButton extends JButton {

    // Ação: executa a rotina 'RoundedButton' desta classe.
    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setForeground(TemaPapelPop.BRANCO);
        setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    // Ação: executa a rotina 'paintComponent' desta classe.
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(TemaPapelPop.AZUL_PRESSIONADO);
        } else if (getModel().isRollover()) {
            g2.setColor(TemaPapelPop.AZUL_HOVER);
        } else {
            g2.setColor(TemaPapelPop.AZUL_PRINCIPAL);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();

        super.paintComponent(g);
    }
}
