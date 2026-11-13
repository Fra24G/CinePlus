package com.cineplus.presentacion;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public final class Tema {
    public static final Color FONDO = new Color(0x160f17),
            TEXTO = new Color(0xf6f1f4),
            MUTED = new Color(0xb3a4af),
            VINO = new Color(0x9e3346);

    private Tema() {}

    public static void instalar() {
        FlatDarkLaf.setup();
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Panel.background", FONDO);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.focusColor", VINO);
        UIManager.put("TextField.background", new Color(0x302632));
        UIManager.put("PasswordField.background", new Color(0x302632));
        UIManager.put("TextField.foreground", TEXTO);
        UIManager.put("PasswordField.foreground", TEXTO);
        UIManager.put("Component.focusWidth", 1);
    }

    public static void preparar(JFrame f) {
        f.setMinimumSize(new Dimension(900, 680));
        if (f.getContentPane().getComponentCount() > 0
                && f.getContentPane().getComponent(0) instanceof JPanel p) {
            p.setBorder(new EmptyBorder(28, 38, 24, 38));
        }
        f.setLocationRelativeTo(null);
    }

    public static void botonSecundario(JButton b) {
        b.setBackground(new Color(0x2a1b26));
        b.setForeground(MUTED);
    }

    public static String escapar(String v) {
        return v.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace(String.valueOf((char) 34), "&quot;");
    }
}
