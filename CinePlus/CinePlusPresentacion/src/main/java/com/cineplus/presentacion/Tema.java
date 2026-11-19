package com.cineplus.presentacion;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.ui.FlatLineBorder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.Icon;
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
 * <p>
 * Los colores salen del Figma (fondo vino muy oscuro, capsulas rojizas, tarjetas moradas
 * y acentos rojos). Las constantes originales (FONDO, TEXTO, MUTED, VINO, TARJETA, CAMPO,
 * ERROR) y sus metodos se conservan porque las usan las pantallas de los demas integrantes.
 *
 * @author luisf
 */
public final class Tema {

    public static final Color FONDO = new Color(0x170e14),
            TEXTO = new Color(0xf6f1f4),
            MUTED = new Color(0xb3a4af),
            VINO = new Color(0x8a2430),
            TARJETA = new Color(0x1d1424),
            CAMPO = new Color(0x433b42),
            ERROR = new Color(0xe0606f);

    // ---------- Paleta del Figma ----------
    /** Texto blanco al 75 % (botones de la barra, etiquetas de los campos). */
    public static final Color TEXTO_SUAVE = new Color(0xc9c2c6);
    /** Placeholder gris de los campos ("INGRESA TU NOMBRE DE USUARIO"). */
    public static final Color PLACEHOLDER = new Color(0x9a949a);
    /** Capsula de la barra de navegacion inferior. */
    public static final Color NAV_FONDO = new Color(0x2a161b);
    public static final Color NAV_BORDE = new Color(0x3d2328);
    /** Pastilla de la seccion actual en la barra inferior. */
    public static final Color NAV_ACTIVO = new Color(0x4a1218);
    public static final Color NAV_ACTIVO_BORDE = new Color(0x6a1d25);
    /** Separador vertical de la barra inferior. */
    public static final Color SEPARADOR = new Color(0x6b5357);
    /** Circulo del avatar con las iniciales. */
    public static final Color AVATAR = new Color(0x6a1c24);
    /** Barra de busqueda tipo pildora. */
    public static final Color BUSCADOR = new Color(0x2a2229);
    /** Botones cuadrados de notificaciones y mas opciones. */
    public static final Color BOTON_ICONO = new Color(0x3c3338);
    /** Botones vino oscuros ("Ver historial de compras", "Vista ADMIN", "Entrar"). */
    public static final Color VINO_OSCURO = new Color(0x4a151b);
    public static final Color VINO_OSCURO_HOVER = new Color(0x5c1b22);
    /** Botones de metodo de pago (TARJETA, APPLE / GOOGLE PAY). */
    public static final Color VINO_METODO = new Color(0x421011);
    /** Boton CONTINUAR (rojo vino mas brillante). */
    public static final Color VINO_BRILLANTE = new Color(0x8a2024);
    /** Pildora "TOTAL - $129 AL MES". */
    public static final Color VINO_TOTAL = new Color(0x5c1620);
    /** Rojo de acento ("Pro", barra de puntos). */
    public static final Color ROJO = new Color(0xea2c2c);
    /** Rojo de la palabra "Pro" en titulos y bordes de tarjetas Pro. */
    public static final Color ROJO_PRO = new Color(0xbc0d0d);
    /** Rojo de "Pro" en la confirmacion. */
    public static final Color ROJO_CONFIRMACION = new Color(0xeb1f2e);
    /** Tarjetas moradas oscuras (membresias, pago, saldo del perfil). */
    public static final Color TARJETA_MORADA = new Color(0x1a1224);
    public static final Color BORDE_TARJETA = new Color(0x5a5560);
    public static final Color BORDE_PRO = new Color(0x7e1418);
    /** Subpanel del formulario de pago. */
    public static final Color SUBPANEL_PAGO = new Color(0x261a20);
    /** Campo gris de los formularios (login, registro, perfil, pago). */
    public static final Color CAMPO_FIGMA = new Color(0x433b42);
    /** Boton gris deshabilitado ("Ya eres miembro"). */
    public static final Color GRIS_BOTON = new Color(0x3e393f);
    /** Enlace rosado grisaceo ("¿No eres tu?"). */
    public static final Color ENLACE = new Color(0x986c6c);
    /** Fondo de la barra de puntos. */
    public static final Color BARRA_FONDO = new Color(0x303235);

    private static final String[] FUENTES_TEXTO = {
        "League Spartan", "Segoe UI"};
    private static final String[] FUENTES_LOGO = {
        "IBM Plex Mono SemiBold", "IBM Plex Mono", "JetBrains Mono", "Cascadia Mono",
        "Consolas", "Lucida Console"};
    private static Set<String> disponibles;

    private Tema() {
    }

    public static void instalar() {
        FlatDarkLaf.setup();
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Panel.background", FONDO);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 12);
        UIManager.put("Component.focusColor", NAV_ACTIVO_BORDE);
        UIManager.put("Component.borderColor", new Color(0x4e454c));
        UIManager.put("TextField.background", CAMPO);
        UIManager.put("PasswordField.background", CAMPO);
        UIManager.put("TextField.foreground", TEXTO);
        UIManager.put("PasswordField.foreground", TEXTO);
        UIManager.put("TextField.placeholderForeground", PLACEHOLDER);
        UIManager.put("PasswordField.placeholderForeground", PLACEHOLDER);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.error.focusedBorderColor", ERROR);
        UIManager.put("Component.error.borderColor", ERROR);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ToolTip.background", NAV_FONDO);
    }

    public static void preparar(JFrame f) {
        f.setMinimumSize(new Dimension(900, 680));
        if (f.getContentPane().getComponentCount() > 0
                && f.getContentPane().getComponent(0) instanceof JPanel p) {
            p.setBorder(new EmptyBorder(22, 30, 16, 30));
            p.setBackground(FONDO);
        }
        f.setLocationRelativeTo(null);
    }

    // ---------- Fuentes ----------

    /** Fuente de los textos (League Spartan del Figma si esta instalada; si no, Segoe UI). */
    public static Font fuente(int estilo, float tamano) {
        return new Font(primeraDisponible(FUENTES_TEXTO, "SansSerif"), estilo, Math.round(tamano));
    }

    /** Fuente monoespaciada del logo "CinePLUS" y del precio "$129". */
    public static Font fuenteLogo(float tamano) {
        return new Font(primeraDisponible(FUENTES_LOGO, Font.MONOSPACED), Font.BOLD, Math.round(tamano));
    }

    /** Etiqueta "CinePLUS" con la fuente monoespaciada del Figma. */
    public static JLabel logo(float tamano) {
        JLabel logo = new JLabel("CinePLUS");
        estiloLogo(logo, tamano);
        return logo;
    }

    /** Aplica la fuente y color del logo a una etiqueta existente. */
    public static void estiloLogo(JLabel etiqueta, float tamano) {
        etiqueta.setFont(fuenteLogo(tamano));
        etiqueta.setForeground(Color.WHITE);
    }

    private static synchronized String primeraDisponible(String[] opciones, String respaldo) {
        if (disponibles == null) {
            disponibles = new HashSet<>();
            try {
                for (String nombre : GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames(Locale.ROOT)) {
                    disponibles.add(nombre.toLowerCase(Locale.ROOT));
                }
            } catch (RuntimeException excepcion) {
                // Sin entorno grafico: se usa el respaldo
            }
        }
        return Arrays.stream(opciones)
                .filter(nombre -> disponibles.contains(nombre.toLowerCase(Locale.ROOT)))
                .findFirst()
                .orElse(respaldo);
    }

    // ---------- Botones ----------

    /** Boton principal (CONTINUAR, Entrar, CREAR): fondo vino, texto blanco y negrita. */
    public static void botonPrimario(JButton b) {
        botonFigma(b, VINO, 14);
        b.setForeground(Color.WHITE);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
    }

    public static void botonSecundario(JButton b) {
        b.putClientProperty("FlatLaf.style", "arc: 14; borderWidth: 0; focusWidth: 0; innerFocusWidth: 0;"
                + " background: #2a1b26; hoverBackground: #3a2632; pressedBackground: #46303c");
        b.setBackground(new Color(0x2a1b26));
        b.setForeground(MUTED);
    }

    /**
     * Boton plano del Figma: sin borde, esquinas redondeadas y un tono mas claro al pasar el
     * mouse. Lo usan los botones vino oscuros, los de metodo de pago y CONTINUAR.
     */
    public static void botonFigma(JButton b, Color fondo, int arco) {
        b.putClientProperty("FlatLaf.style", "arc: " + arco
                + "; borderWidth: 0; focusWidth: 0; innerFocusWidth: 0"
                + "; background: " + hex(fondo)
                + "; hoverBackground: " + hex(aclarar(fondo, 0.12f))
                + "; pressedBackground: " + hex(aclarar(fondo, 0.22f))
                + "; disabledBackground: " + hex(fondo)
                + "; disabledText: #8d868b");
        b.setBackground(fondo);
        b.setForeground(new Color(0xf1eaee));
        b.setFocusable(false);
        b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    /** Boton vino con un borde casi negro, como "Entrar", "Registrar" y la capsula "CinePLUS". */
    public static void botonContorno(JButton b, Color fondo, Color borde, int arco) {
        b.putClientProperty("FlatLaf.style", "arc: " + arco
                + "; borderWidth: 1; focusWidth: 0; innerFocusWidth: 0"
                + "; borderColor: " + hex(borde)
                + "; hoverBorderColor: " + hex(aclarar(borde, 0.25f))
                + "; background: " + hex(fondo)
                + "; hoverBackground: " + hex(aclarar(fondo, 0.12f))
                + "; pressedBackground: " + hex(aclarar(fondo, 0.22f)));
        b.setBackground(fondo);
        b.setForeground(new Color(0xe9dfe3));
        b.setFocusable(false);
        b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    /** Boton sin fondo que parece enlace (por ejemplo "¿No eres tu?" o "¿Ya tienes cuenta?"). */
    public static void enlace(JButton b, Color color, boolean subrayado) {
        b.putClientProperty("JButton.buttonType", "borderless");
        b.putClientProperty("FlatLaf.style", "focusWidth: 0; innerFocusWidth: 0; margin: 2,4,2,4");
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusable(false);
        b.setForeground(color);
        b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        if (subrayado) {
            String texto = b.getText();
            if (texto != null && !texto.startsWith("<html>")) {
                b.setText("<html><u>" + escapar(texto) + "</u></html>");
            }
        }
    }

    /** Boton cuadrado redondeado del encabezado (campana y "..."). */
    public static JButton botonIcono(String texto, Icon icono, String ayuda) {
        JButton boton = new JButton(texto, icono);
        boton.setToolTipText(ayuda);
        boton.getAccessibleContext().setAccessibleName(ayuda);
        boton.setFont(fuente(Font.BOLD, 15));
        boton.setMargin(new Insets(0, 0, 0, 0));
        botonFigma(boton, BOTON_ICONO, 14);
        boton.setForeground(new Color(0xd7cfd4));
        return boton;
    }

    // ---------- Textos ----------

    /** Titulo de pantalla ("Historial de Compras", "Tu Resena:"). */
    public static void titulo(JLabel etiqueta) {
        etiqueta.setFont(fuente(Font.BOLD, 26));
        etiqueta.setForeground(TEXTO);
    }

    /**
     * HTML con la palabra "Pro" pintada de color (por ejemplo "¡Ya eres Pro! Bienvenido").
     * El resto del texto se escapa.
     */
    public static String resaltarPro(String texto, Color color) {
        if (texto == null) {
            return "";
        }
        String seguro = escapar(texto);
        return "<html>" + seguro.replaceAll("\\bPro\\b",
                "<font color='" + hex(color) + "'>Pro</font>") + "</html>";
    }

    // ---------- Paneles ----------

    /** Panel con fondo un poco mas claro, esquinas redondeadas y margen interno. */
    public static void tarjeta(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(TARJETA);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.putClientProperty("FlatLaf.style", "arc: 18");
    }

    /**
     * Tarjeta del Figma: fondo solido, esquinas redondeadas y borde de 1 px (blanco sutil en
     * CinePLUS+, rojo en CinePLUS Pro y en el pago).
     */
    public static void tarjetaConBorde(JPanel panel, Color fondo, Color borde, int arco, Insets margen) {
        panel.setOpaque(true);
        panel.setBackground(fondo);
        panel.putClientProperty("FlatLaf.style", "arc: " + arco);
        panel.setBorder(borde == null
                ? new EmptyBorder(margen)
                : new FlatLineBorder(margen, borde, 1f, arco));
    }

    // ---------- Campos ----------

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

    /**
     * Campo grande del Figma: fondo gris, sin borde visible, esquinas redondeadas y
     * placeholder en mayusculas (CORREO, CONTRASEÑA, NOMBRE DEL TITULAR...).
     */
    public static void campoFigma(JTextField campo, String ejemplo, int arco, int alto) {
        campo.putClientProperty("JTextField.placeholderText", ejemplo);
        campo.putClientProperty("FlatLaf.style", "arc: " + arco
                + "; borderWidth: 0; focusWidth: 0; innerFocusWidth: 0"
                + "; borderColor: " + hex(CAMPO_FIGMA)
                + "; focusedBorderColor: " + hex(NAV_ACTIVO_BORDE)
                + "; background: " + hex(CAMPO_FIGMA)
                + "; margin: 2,16,2,16");
        campo.setBackground(CAMPO_FIGMA);
        campo.setForeground(TEXTO);
        campo.setCaretColor(TEXTO);
        campo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        campo.setPreferredSize(new Dimension(Math.max(60, campo.getPreferredSize().width), alto));
        campo.setMinimumSize(new Dimension(60, alto));
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

    // ---------- Colores ----------

    /** "#rrggbb" para los estilos de FlatLaf y el HTML de las etiquetas. */
    public static String hex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /** Mezcla el color con blanco (0 = igual, 1 = blanco). */
    public static Color aclarar(Color color, float cantidad) {
        return new Color(
                Math.min(255, Math.round(color.getRed() + (255 - color.getRed()) * cantidad)),
                Math.min(255, Math.round(color.getGreen() + (255 - color.getGreen()) * cantidad)),
                Math.min(255, Math.round(color.getBlue() + (255 - color.getBlue()) * cantidad)));
    }

    // ---------- Iconos dibujados (no dependen de fuentes con emoji) ----------

    /** Campana de notificaciones. */
    public static Icon iconoCampana(int tamano, Color color) {
        return new IconoDibujado(tamano, color) {
            @Override
            void dibujar(Graphics2D g, float s) {
                Path2D campana = new Path2D.Float();
                campana.moveTo(s * 0.50, s * 0.12);
                campana.curveTo(s * 0.28, s * 0.12, s * 0.24, s * 0.32, s * 0.24, s * 0.48);
                campana.lineTo(s * 0.24, s * 0.64);
                campana.lineTo(s * 0.14, s * 0.76);
                campana.lineTo(s * 0.86, s * 0.76);
                campana.lineTo(s * 0.76, s * 0.64);
                campana.lineTo(s * 0.76, s * 0.48);
                campana.curveTo(s * 0.76, s * 0.32, s * 0.72, s * 0.12, s * 0.50, s * 0.12);
                campana.closePath();
                g.fill(campana);
                g.fill(new Ellipse2D.Float(s * 0.41f, s * 0.78f, s * 0.18f, s * 0.14f));
            }
        };
    }

    /** Lapiz de "editar" (avatar y nombre de usuario del perfil). */
    public static Icon iconoLapiz(int tamano, Color color) {
        return new IconoDibujado(tamano, color) {
            @Override
            void dibujar(Graphics2D g, float s) {
                Graphics2D r = (Graphics2D) g.create();
                r.rotate(Math.toRadians(-45), s * 0.5, s * 0.45);
                r.fill(new RoundRectangle2D.Float(s * 0.40f, s * 0.02f, s * 0.20f, s * 0.14f, 3, 3));
                r.fill(new RoundRectangle2D.Float(s * 0.40f, s * 0.19f, s * 0.20f, s * 0.48f, 2, 2));
                Path2D punta = new Path2D.Float();
                punta.moveTo(s * 0.40, s * 0.70);
                punta.lineTo(s * 0.60, s * 0.70);
                punta.lineTo(s * 0.50, s * 0.86);
                punta.closePath();
                r.fill(punta);
                r.dispose();
                g.fill(new RoundRectangle2D.Float(s * 0.08f, s * 0.88f, s * 0.84f, s * 0.09f, 3, 3));
            }
        };
    }

    /** Lupa del buscador. */
    public static Icon iconoLupa(int tamano, Color color) {
        return new IconoDibujado(tamano, color) {
            @Override
            void dibujar(Graphics2D g, float s) {
                g.setStroke(new BasicStroke(Math.max(1.6f, s * 0.12f),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Ellipse2D.Float(s * 0.14f, s * 0.14f, s * 0.50f, s * 0.50f));
                g.drawLine(Math.round(s * 0.58f), Math.round(s * 0.58f),
                        Math.round(s * 0.86f), Math.round(s * 0.86f));
            }
        };
    }

    /** Base de los iconos vectoriales: antialias y color fijo. */
    private abstract static class IconoDibujado implements Icon {

        private final int tamano;
        private final Color color;

        IconoDibujado(int tamano, Color color) {
            this.tamano = tamano;
            this.color = color;
        }

        abstract void dibujar(Graphics2D g, float s);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g2.translate(x, y);
                g2.setColor(c != null && !c.isEnabled() ? color.darker() : color);
                dibujar(g2, tamano);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public int getIconWidth() {
            return tamano;
        }

        @Override
        public int getIconHeight() {
            return tamano;
        }
    }
}
