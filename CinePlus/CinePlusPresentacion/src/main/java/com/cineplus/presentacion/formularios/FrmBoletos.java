package com.cineplus.presentacion.formularios;

import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;
import com.cineplus.presentacion.TarjetaBoleto;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Pantalla "Vista de boletos" (F9): tarjetas de los boletos vigentes con paginador
 * "&lt; 1 - 2 - 3 &gt;", y su version vacia "No tienes compras actualmente..." con el
 * boton Ir a Inicio.
 *
 * @author Mario Perez
 */
public class FrmBoletos extends JFrame {

    private static final String LISTA = "lista";
    private static final String VACIO = "vacio";
    /** Cuantos numeros de pagina se muestran como maximo en el paginador. */
    private static final int PAGINAS_VISIBLES = 7;

    private CoordinadorAplicacion coordinador;
    private final JPanel pnlTarjetas = new JPanel(new GridLayout(0, 2, 16, 16));
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel pnlVistas = new JPanel(tarjetas);
    private final JButton btnIrInicio = new JButton("Ir a Inicio");
    private int paginaActual;
    private int totalPaginas;
    private boolean paginadorActivo = true;

    /** Constructor para el editor visual de NetBeans. */
    public FrmBoletos() {
        initComponents();
        configurarVista();
    }

    /** Arma la vista y pide la primera pagina de boletos. */
    public FrmBoletos(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        pnlNavegacion.add(new PanelNavegacion(coordinador, Seccion.BOLETOS), BorderLayout.CENTER);
        btnIrInicio.addActionListener(e -> coordinador.inicio());
        coordinador.boletos().cargarBoletos(this, 0);
    }

    /** Estilos con Tema; las tarjetas y el estado vacio se arman aqui. */
    private void configurarVista() {
        Tema.preparar(this);
        setTitle("CinePlus · Mis boletos");
        pnlCabecera.add(new PanelEncabezado(null), BorderLayout.CENTER);
        Tema.titulo(lblTitulo);

        pnlTarjetas.setOpaque(false);
        JPanel contenedor = new PanelAjustado();
        contenedor.add(pnlTarjetas, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(contenedor,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        pnlVistas.setOpaque(false);
        pnlVistas.add(scroll, LISTA);
        pnlVistas.add(crearVacio(), VACIO);
        pnlCuerpo.add(pnlVistas, BorderLayout.CENTER);

        lblError.setForeground(Tema.ERROR);
        lblEstado.setForeground(Tema.MUTED);
    }

    private JPanel crearVacio() {
        JPanel vacio = new JPanel(new GridBagLayout());
        vacio.setOpaque(false);
        JLabel lblVacio = new JLabel("No tienes compras actualmente...", SwingConstants.CENTER);
        lblVacio.setFont(Tema.fuente(Font.BOLD, 30));
        lblVacio.setForeground(Tema.TEXTO_SUAVE);
        Tema.botonFigma(btnIrInicio, Tema.VINO_OSCURO, 20);
        btnIrInicio.setFont(Tema.fuente(Font.BOLD, 16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 18, 0);
        vacio.add(lblVacio, gbc);
        gbc.gridy = 1;
        gbc.ipadx = 40;
        gbc.ipady = 14;
        vacio.add(btnIrInicio, gbc);
        return vacio;
    }

    /**
     * Panel que siempre toma el ancho visible del scroll: las tarjetas se acomodan
     * al ancho de la ventana y solo hay barra vertical.
     */
    private static final class PanelAjustado extends JPanel implements Scrollable {

        PanelAjustado() {
            super(new BorderLayout());
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible, int orientacion, int direccion) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible, int orientacion, int direccion) {
            return visible.height;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    /** Una TarjetaBoleto por boleto y el paginador. Si no hay boletos muestra el estado vacio. */
    public void mostrarBoletos(PaginaDTO<BoletoDTO> pagina) {
        pnlTarjetas.removeAll();
        for (BoletoDTO boleto : pagina.elementos()) {
            pnlTarjetas.add(new TarjetaBoleto(boleto, coordinador));
        }
        tarjetas.show(pnlVistas, pagina.total() == 0 ? VACIO : LISTA);
        paginaActual = pagina.pagina();
        totalPaginas = pagina.totalPaginas();
        dibujarPaginador();
        pnlTarjetas.revalidate();
        pnlTarjetas.repaint();
    }

    /** Paginador del Figma "&lt; 1 - 2 - 3 &gt;" dentro de pnlPaginador. */
    private void dibujarPaginador() {
        pnlPaginador.removeAll();
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        botones.setOpaque(false);
        if (totalPaginas > 1) {
            botones.add(botonPagina("<", paginaActual - 1, paginaActual > 0, false));
            int inicio = Math.max(0, Math.min(paginaActual - PAGINAS_VISIBLES / 2, totalPaginas - PAGINAS_VISIBLES));
            int fin = Math.min(totalPaginas, inicio + PAGINAS_VISIBLES);
            for (int i = inicio; i < fin; i++) {
                if (i > inicio) {
                    JLabel guion = new JLabel("-");
                    guion.setForeground(Tema.MUTED);
                    botones.add(guion);
                }
                botones.add(botonPagina(String.valueOf(i + 1), i, true, i == paginaActual));
            }
            botones.add(botonPagina(">", paginaActual + 1, paginaActual < totalPaginas - 1, false));
        }
        pnlPaginador.add(botones, BorderLayout.CENTER);
        pnlPaginador.revalidate();
        pnlPaginador.repaint();
    }

    /** Boton de una pagina; al pulsarlo pide esa pagina al coordinador. */
    private JButton botonPagina(String texto, int destino, boolean activo, boolean actual) {
        JButton boton = new JButton(texto);
        Tema.enlace(boton, actual ? Tema.TEXTO : Tema.MUTED, actual);
        boton.setFont(Tema.fuente(actual ? Font.BOLD : Font.PLAIN, 16));
        boton.setEnabled(activo && paginadorActivo);
        boton.addActionListener(e -> {
            if (!actual) {
                paginaActual = destino;
                coordinador.boletos().cargarBoletos(this, destino);
            }
        });
        return boton;
    }

    /** Deshabilita el paginador y muestra "Cargando…" mientras corre la tarea. */
    public void ocupado(boolean ocupado) {
        paginadorActivo = !ocupado;
        dibujarPaginador();
        btnIrInicio.setEnabled(!ocupado);
        lblEstado.setText(ocupado ? "Cargando…" : "");
        if (ocupado) {
            mostrarError(null);
        }
    }

    /** Muestra el mensaje de la Exception en la etiqueta roja (null la limpia). */
    public void mostrarError(String mensaje) {
        lblError.setText(mensaje == null || mensaje.isBlank()
                ? ""
                : "<html>" + Tema.escapar(mensaje) + "</html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();
        pnlCabecera = new javax.swing.JPanel();
        pnlContenido = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        pnlCuerpo = new javax.swing.JPanel();
        pnlPie = new javax.swing.JPanel();
        lblError = new javax.swing.JLabel();
        pnlPaginador = new javax.swing.JPanel();
        lblEstado = new javax.swing.JLabel();
        pnlNavegacion = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlFondo.setBackground(new java.awt.Color(23, 14, 20));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        pnlCabecera.setOpaque(false);
        pnlCabecera.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlCabecera, java.awt.BorderLayout.NORTH);

        pnlContenido.setOpaque(false);
        pnlContenido.setLayout(new java.awt.BorderLayout(12, 12));

        lblTitulo.setText("Mis boletos");
        pnlContenido.add(lblTitulo, java.awt.BorderLayout.NORTH);

        pnlCuerpo.setOpaque(false);
        pnlCuerpo.setLayout(new java.awt.BorderLayout());
        pnlContenido.add(pnlCuerpo, java.awt.BorderLayout.CENTER);

        pnlPie.setOpaque(false);
        pnlPie.setLayout(new java.awt.BorderLayout(8, 8));

        lblError.setText("");
        pnlPie.add(lblError, java.awt.BorderLayout.NORTH);

        pnlPaginador.setOpaque(false);
        pnlPaginador.setLayout(new java.awt.BorderLayout());
        pnlPie.add(pnlPaginador, java.awt.BorderLayout.CENTER);

        lblEstado.setText("");
        pnlPie.add(lblEstado, java.awt.BorderLayout.SOUTH);

        pnlContenido.add(pnlPie, java.awt.BorderLayout.SOUTH);

        pnlFondo.add(pnlContenido, java.awt.BorderLayout.CENTER);

        pnlNavegacion.setOpaque(false);
        pnlNavegacion.setLayout(new java.awt.BorderLayout());
        pnlFondo.add(pnlNavegacion, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblError;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlCabecera;
    private javax.swing.JPanel pnlContenido;
    private javax.swing.JPanel pnlCuerpo;
    private javax.swing.JPanel pnlFondo;
    private javax.swing.JPanel pnlNavegacion;
    private javax.swing.JPanel pnlPaginador;
    private javax.swing.JPanel pnlPie;
    // End of variables declaration//GEN-END:variables
}
