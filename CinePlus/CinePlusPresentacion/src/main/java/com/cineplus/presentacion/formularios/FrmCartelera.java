
package com.cineplus.presentacion.formularios;

import com.cineplus.dto.PeliculaDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.TarjetaPelicula;
import com.cineplus.presentacion.Tema;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla de cartelera de CinePlus
 *
 * @author Juan Ramon Acuña Gutierrez
 */
public class FrmCartelera extends JFrame {

    private CoordinadorAplicacion coordinador;

    private PanelEncabezado encabezado;
    private PanelNavegacion navegacion;

    private JPanel pnlTarjetas;
    private JPanel pnlResultados;

    private JScrollPane scrollTarjetas;

    private JLabel lblEstado;
    private JLabel lblVacio;

    private List<PeliculaDTO> peliculas = new ArrayList<>();
    private String busqueda = "";

    public FrmCartelera() {
        initComponents();
        configurarVentana();
    }

    public FrmCartelera(CoordinadorAplicacion coordinador) {
        this();
        this.coordinador = coordinador;
        construirPantalla();
    }

    /** Configura las propiedades generales de la ventana */
    private void configurarVentana() {
        setTitle("CinePlus · Cartelera");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pnlFondo.setBackground(Tema.FONDO);
        pnlFondo.setPreferredSize(new Dimension(1110, 740));

        Tema.preparar(this);
    }

    /** Construye la pantalla utilizando los componentes compartidos */
    private void construirPantalla() {

        // Encabezado con logo y buscador
        encabezado = new PanelEncabezado(this::filtrar);

        // Barra inferior de navegacion
        navegacion = new PanelNavegacion(
                coordinador, Seccion.CARTELERA);

        // Titulo y estado de la cartelera
        JPanel pnlEncabezado = new JPanel(
                new BorderLayout(0, 8));
        pnlEncabezado.setOpaque(false);

        JLabel lblTitulo = new JLabel("Películas de la semana");
        lblTitulo.setFont(
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        lblTitulo.setForeground(Tema.TEXTO);

        lblEstado = new JLabel(" ");
        lblEstado.setForeground(Tema.MUTED);
        lblEstado.setVisible(false);

        pnlEncabezado.add(lblTitulo, BorderLayout.NORTH);
        pnlEncabezado.add(lblEstado, BorderLayout.SOUTH);

        // Cuadricula donde se muestran las peliculass
        pnlTarjetas = new JPanel(new GridLayout(0, 5, 20, 24));
        pnlTarjetas.setOpaque(false);
        pnlTarjetas.setBorder(
                new EmptyBorder(8, 8, 18, 8));

        scrollTarjetas = new JScrollPane(pnlTarjetas);
        scrollTarjetas.setBorder(BorderFactory.createEmptyBorder());
        scrollTarjetas.setOpaque(false);
        scrollTarjetas.getViewport().setOpaque(false);
        scrollTarjetas.getVerticalScrollBar().setUnitIncrement(18);

        // Mensaje para cuando no hay resultados
        lblVacio = new JLabel(
                "No hay películas en cartelera.",
                SwingConstants.CENTER);
        lblVacio.setForeground(Tema.MUTED);
        lblVacio.setFont(
                new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 15));

        // Permite alternar entre las tarjetas y el mensaje vacio
        pnlResultados = new JPanel(new CardLayout());
        pnlResultados.setOpaque(false);
        pnlResultados.add(scrollTarjetas, "peliculas");
        pnlResultados.add(lblVacio, "vacio");

        // Contenido central
        JPanel pnlContenido = new JPanel(
                new BorderLayout(0, 16));
        pnlContenido.setOpaque(false);
        pnlContenido.add(pnlEncabezado, BorderLayout.NORTH);
        pnlContenido.add(pnlResultados, BorderLayout.CENTER);

        // Organizacion general
        pnlFondo.add(encabezado, BorderLayout.NORTH);
        pnlFondo.add(pnlContenido, BorderLayout.CENTER);
        pnlFondo.add(navegacion, BorderLayout.SOUTH);

        Tema.preparar(this);
        pack();
        setLocationRelativeTo(null);
    }

    /** Recibe las peliculas de la cartelera local */
    public void mostrarPeliculas(List<PeliculaDTO> peliculas) {

        this.peliculas = peliculas == null
                ? new ArrayList<>()
                : new ArrayList<>(peliculas);

        lblEstado.setText(" ");
        lblEstado.setVisible(false);

        actualizarTarjetas();
    }

    /** Filtra las peliculas por su titulo */
    private void filtrar(String texto) {
        busqueda = texto == null ? "" : texto;
        actualizarTarjetas();
    }

    /** Actualiza las tarjetas que se muestran en pantalla*/
    private void actualizarTarjetas() {

        if (pnlTarjetas == null) {
            return;
        }

        pnlTarjetas.removeAll();

        String criterio = busqueda.strip()
                .toLowerCase(Locale.ROOT);

        List<PeliculaDTO> visibles = peliculas.stream()
                .filter(pelicula -> pelicula != null
                        && pelicula.titulo() != null
                        && pelicula.titulo()
                                .toLowerCase(Locale.ROOT)
                                .contains(criterio))
                .toList();

        // Crea una tarjeta por cada película encontrada
        for (PeliculaDTO pelicula : visibles) {

            TarjetaPelicula tarjeta = new TarjetaPelicula(
                    pelicula,
                    coordinador,
                    seleccionada -> coordinador.cartelera()
                            .abrirFunciones(seleccionada)
            );

            pnlTarjetas.add(tarjeta);
        }

        // Muestra las películas o el mensaje correspondiente
        if (visibles.isEmpty()) {

            if (criterio.isEmpty()) {
                lblVacio.setText(
                        "No hay películas en cartelera.");
            } else {
                lblVacio.setText(
                        "No encontramos películas con ese nombre.");
            }

            mostrarResultado("vacio");

        } else {
            mostrarResultado("peliculas");
        }

        pnlTarjetas.revalidate();
        pnlTarjetas.repaint();
        pnlResultados.revalidate();
        pnlResultados.repaint();
    }

    /** Cambia entre la cuadricula y el mensaje vacio */
    private void mostrarResultado(String nombre) {

        CardLayout layout =
                (CardLayout) pnlResultados.getLayout();

        layout.show(pnlResultados, nombre);
    }

    /** Controla el estado de carga de la pantalla */
    public void ocupado(boolean ocupado) {

        if (ocupado) {

            lblEstado.setText("Cargando películas...");
            lblEstado.setForeground(Tema.MUTED);
            lblEstado.setVisible(true);

            cambiarHabilitacion(encabezado, false);
            cambiarHabilitacion(navegacion, false);

            setCursor(Cursor.getPredefinedCursor(
                    Cursor.WAIT_CURSOR));

        } else {

            cambiarHabilitacion(encabezado, true);

            if (navegacion != null) {
                navegacion.actualizar();
            }

            setCursor(Cursor.getDefaultCursor());

            if ("Cargando películas...".equals(lblEstado.getText())) {
                lblEstado.setText(" ");
                lblEstado.setVisible(false);
            }
        }
    }

    /** Muestra los errores recibidos desde la capa de negocio */
    public void mostrarError(String mensaje) {

        lblEstado.setText(
                mensaje == null || mensaje.isBlank()
                ? "Ocurrió un error al cargar la cartelera."
                : mensaje
        );

        lblEstado.setForeground(Tema.ERROR);
        lblEstado.setVisible(true);
    }

    /** Habilita o deshabilita los componentes de un panel */
    private void cambiarHabilitacion(
            Component componente, boolean habilitado) {

        if (componente == null) {
            return;
        }

        componente.setEnabled(habilitado);

        if (componente instanceof Container contenedor) {
            for (Component hijo : contenedor.getComponents()) {
                cambiarHabilitacion(hijo, habilitado);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlFondo = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.BorderLayout());

        pnlFondo.setBackground(new java.awt.Color(22, 15, 23));
        pnlFondo.setPreferredSize(new java.awt.Dimension(1110, 740));
        pnlFondo.setLayout(new java.awt.BorderLayout(16, 16));

        getContentPane().add(pnlFondo, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlFondo;
    // End of variables declaration//GEN-END:variables
}
