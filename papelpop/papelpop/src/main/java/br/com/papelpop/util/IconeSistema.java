package br.com.papelpop.util;

import javax.swing.*;
import java.awt.*;

public class IconeSistema {

    private static final String CAMINHO_ICONE = "C:/papelpop/icons/papelpop.jpg";

    public static void aplicarIcone(JFrame frame) {

        try {
            ImageIcon icon = new ImageIcon(CAMINHO_ICONE);

            Image imgRedimensionada = icon.getImage()
                    .getScaledInstance(32, 32, Image.SCALE_SMOOTH);

            frame.setIconImage(imgRedimensionada);

        } catch (Exception e) {
            System.out.println("Erro ao carregar ícone do sistema: " + e.getMessage());
        }
    }
}