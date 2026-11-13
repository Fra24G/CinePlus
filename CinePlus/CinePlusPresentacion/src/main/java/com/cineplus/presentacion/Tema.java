package com.cineplus.presentacion;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Colores y estilos comunes de CinePlus. Todas las pantallas usan estas ayudas para verse
 * iguales. Despues del Flujo 0 solo lo modifica el Desarrollador 1 (un estilo nuevo se le pide).
 *
 * @author luisf
 */
public final class Tema {

    public static final Color FONDO = new Color(0x160f17),
            TEXTO = new Color(0xf6f1f4),
            MUTED = new Color(0xb3a4af),
            VINO = new Color(0x9e3346),
            TARJETA = new Color(0x231826),
            CAMPO = new Color(0x302632),
            ERROR = new Color(0xe0606f);

    private Tema() {
    }

    public static void instalar() {
        FlatDarkLaf.setup();
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Panel.background", FONDO);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.focusColor", VINO);
        UIManager.put("TextField.background", CAMPO);
        UIManager.put("PasswordField.background", CAMPO);
        UIManager.put("TextField.foreground", TEXTO);
        UIManager.put("PasswordField.foreground", TEXTO);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.error.focusedBorderColor", ERROR);
        UIManager.put("Component.error.borderColor", ERROR);
    }

    public static void preparar(JFrame f) {
        f.setMinimumSize(new Dimension(900, 680));
        if (f.getContentPane().getComponentCount() > 0
                && f.getContentPane().getComponent(0) instanceof JPanel p) {
            p.setBorder(new EmptyBorder(28, 38, 24, 38));
        }
        f.setLocationRelativeTo(null);
    }

    /** Boton principal (CONTINUAR, Entrar, CREAR): fondo vino, texto blanco y negrita. */
    public static void botonPrimario(JButton b) {
        b.setBackground(VINO);
        b.setForeground(Color.WHITE);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        b.putClientProperty("FlatLaf.style", "arc: 14");
    }

    public static void botonSecundario(JButton b) {
        b.setBackground(new Color(0x2a1b26));
        b.setForeground(MUTED);
    }

    /** Titulo de pantalla ("Historial de Compras", "Tu Resena:"). */
    public static void titulo(JLabel etiqueta) {
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 24));
        etiqueta.setForeground(TEXTO);
    }

    /** Panel con fondo un poco mas claro, esquinas redondeadas y margen interno. */
    public static void tarjeta(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(TARJETA);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.putClientProperty("FlatLaf.style", "arc: 18");
    }

    /** Texto de ejemplo (placeholder) y estilo comun de los campos de captura. */
    public static void campo(JTextField campo, String ejemplo) {
        campo.putClientProperty("JTextField.placeholderText", ejemplo);
        campo.setBackground(CAMPO);
        campo.setForeground(TEXTO);
        campo.setCaretColor(TEXTO);
        if (campo.getPreferredSize().height < 36) {
            campo.setPreferredSize(new Dimension(campo.getPreferredSize().width, 36));
        }
    }

    /** Pinta el borde del campo en rojo cuando una validacion falla (o lo quita). */
    public static void marcarError(JComponent campo, boolean error) {
        campo.putClientProperty("JComponent.outline", error ? "error" : null);
        campo.repaint();
    }

    public static String escapar(String v) {
        if (v == null) {
            return "";
        }
        return v.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace(String.valueOf((char) 34), "&quot;");
    }
}
