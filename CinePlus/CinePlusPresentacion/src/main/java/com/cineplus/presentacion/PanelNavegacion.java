package com.cineplus.presentacion;

import com.cineplus.dto.SesionDTO;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Barra inferior tipo capsula de las pantallas del cliente: Inicio, Cartelera, Boletos,
 * Historial, Comunidad, separador vertical y, segun haya sesion o no, CinePLUS + avatar circular
 * o los botones "Iniciar sesion" y "Registrar cuenta". Uso en una pantalla:
 * <pre>pnlPie.add(new PanelNavegacion(coordinador, Seccion.BOLETOS));</pre>
 *
 * @author luisf
 */
public final class PanelNavegacion extends JPanel {

    private final CoordinadorAplicacion coordinador;
    private final Seccion actual;
    private final Map<Seccion, JButton> botones = new EnumMap<>(Seccion.class);
    private final JButton btnIniciarSesion;
    private final JButton btnRegistrarCuenta;

    public PanelNavegacion(CoordinadorAplicacion coordinador, Seccion actual) {
        super(new FlowLayout(FlowLayout.CENTER, 0, 2));
        this.coordinador = coordinador;
        this.actual = actual;
        setOpaque(false);

        Capsula capsula = new Capsula();
        for (Seccion seccion : Seccion.values()) {
            if (seccion == Seccion.CINEPLUS) {
                capsula.add(new SeparadorVertical());
            }
            JButton boton = seccion == Seccion.PERFIL
                    ? new BotonAvatar()
                    : new BotonSeccion(seccion.texto());
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.setFocusable(false);
            boton.addActionListener(e -> {
                if (this.coordinador != null) {
                    this.coordinador.navegar(seccion);
                }
            });
            botones.put(seccion, boton);
            capsula.add(boton);
        }

        btnIniciarSesion = crearBotonInvitado("<html><center>Iniciar<br>sesion</center></html>");
        btnIniciarSesion.addActionListener(e -> {
            if (this.coordinador != null) {
                this.coordinador.login();
            }
        });
        btnRegistrarCuenta = crearBotonInvitado("<html><center>Registrar<br>cuenta</center></html>");
        btnRegistrarCuenta.addActionListener(e -> {
            if (this.coordinador != null) {
                this.coordinador.registro();
            }
        });
        capsula.add(btnIniciarSesion);
        capsula.add(btnRegistrarCuenta);

        add(capsula);
        actualizar();
    }

    /** Vuelve a leer la sesion: muestra CinePLUS + avatar o los botones de invitado. */
    public void actualizar() {
        SesionDTO sesion = coordinador == null ? null : coordinador.sesion();
        boolean conSesion = sesion != null;

        for (Map.Entry<Seccion, JButton> entrada : botones.entrySet()) {
            Seccion seccion = entrada.getKey();
            JButton boton = entrada.getValue();
            if (boton instanceof BotonSeccion bs) {
                // En invitado (FrmInicio) el Figma muestra todos los enlaces limpios sin pastilla
                bs.setActivo(seccion == actual && (conSesion || seccion != Seccion.INICIO));
            } else if (boton instanceof BotonAvatar av) {
                av.setActivo(seccion == actual);
            }
        }

        JButton btnCinePlus = botones.get(Seccion.CINEPLUS);
        JButton avatar = botones.get(Seccion.PERFIL);
        if (btnCinePlus != null) {
            btnCinePlus.setVisible(conSesion);
        }
        if (avatar != null) {
            avatar.setVisible(conSesion);
            avatar.setText(conSesion ? iniciales(sesion.usuario().nombre()) : "YR");
            avatar.setToolTipText(conSesion ? sesion.usuario().nombre() : "Inicia sesión");
        }
        btnIniciarSesion.setVisible(!conSesion);
        btnRegistrarCuenta.setVisible(!conSesion);
        revalidate();
        repaint();
    }

    private static JButton crearBotonInvitado(String html) {
        JButton boton = new JButton(html) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth();
                    int h = getHeight();
                    g2.setColor(getModel().isRollover() ? new Color(0x52161c) : new Color(0x3e1015));
                    g2.fillRoundRect(0, 0, w - 1, h - 1, 18, 18);
                    g2.setColor(new Color(0x160709));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);
                } finally {
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setFocusable(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setFont(Tema.fuente(Font.BOLD, 14));
        boton.setForeground(new Color(0xdad2d6));
        boton.setPreferredSize(new Dimension(112, 40));
        return boton;
    }

    /** "Yazid Enrique Revilla Bernal" da "YR". */
    static String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "?";
        }
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length == 1) {
            return partes[0].substring(0, Math.min(2, partes[0].length())).toUpperCase();
        }
        // Si tiene 3 o mas palabras (ej. "Yazid Enrique Revilla Bernal"), usa primer nombre y primer apellido
        int segundoIndice = partes.length >= 3 ? 2 : 1;
        return ("" + Character.toUpperCase(partes[0].charAt(0))
                + Character.toUpperCase(partes[segundoIndice].charAt(0)));
    }

    /** Contenedor en forma de pildora oscura rojiza como en el Figma. */
    private static final class Capsula extends JPanel {

        Capsula() {
            super(new FlowLayout(FlowLayout.CENTER, 12, 6));
            setOpaque(false);
            setBorder(new EmptyBorder(1, 18, 1, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(Tema.NAV_FONDO);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 30, 30);
                g2.setColor(Tema.NAV_BORDE);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 30, 30);
            } finally {
                g2.dispose();
            }
        }
    }

    /** Linea vertical sutil antes de CinePLUS o de los botones de invitado. */
    private static final class SeparadorVertical extends JComponent {

        SeparadorVertical() {
            setPreferredSize(new Dimension(10, 36));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.SEPARADOR);
                int x = getWidth() / 2;
                g2.fillRoundRect(x, 3, 2, getHeight() - 6, 2, 2);
            } finally {
                g2.dispose();
            }
        }
    }

    /** Boton de texto limpio; solo pinta la pastilla vino oscuro cuando es la seccion activa. */
    private static final class BotonSeccion extends JButton {

        private boolean activo;

        BotonSeccion(String texto) {
            super(texto);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(Tema.fuente(Font.BOLD, 16));
            setForeground(Tema.TEXTO_SUAVE);
            setMargin(new Insets(6, 16, 6, 16));
            setBorder(new EmptyBorder(7, 18, 7, 18));
        }

        void setActivo(boolean activo) {
            this.activo = activo;
            setForeground(activo ? new Color(0xf0e9ec) : Tema.TEXTO_SUAVE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (activo || getModel().isRollover()) {
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth();
                    int h = getHeight();
                    g2.setColor(activo ? new Color(0x410e13) : new Color(0x34181d));
                    g2.fillRoundRect(1, 2, w - 3, h - 4, 16, 16);
                    if (activo) {
                        g2.setColor(new Color(0x1b0507));
                        g2.drawRoundRect(1, 2, w - 3, h - 4, 16, 16);
                    }
                } finally {
                    g2.dispose();
                }
            }
            super.paintComponent(g);
        }
    }

    /** Circulo color vino con las iniciales del usuario ("YR"). */
    private static final class BotonAvatar extends JButton {

        private boolean activo;

        BotonAvatar() {
            super("YR");
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFont(Tema.fuente(Font.BOLD, 15));
            setForeground(new Color(0xe7dfe2));
            setPreferredSize(new Dimension(42, 42));
        }

        void setActivo(boolean activo) {
            this.activo = activo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int d = Math.min(getWidth(), getHeight()) - 2;
                int x = (getWidth() - d) / 2;
                int y = (getHeight() - d) / 2;
                g2.setColor(getModel().isRollover()
                        ? Tema.aclarar(Tema.AVATAR, 0.12f)
                        : Tema.AVATAR);
                g2.fillOval(x, y, d, d);
                if (activo) {
                    g2.setColor(new Color(0xb5303a));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(x, y, d, d);
                }
                g2.setFont(getFont());
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText() == null ? "" : getText();
                int tx = (getWidth() - fm.stringWidth(txt)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(txt, tx, ty);
            } finally {
                g2.dispose();
            }
        }
    }
}
