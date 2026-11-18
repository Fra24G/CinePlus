/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.presentacion.formularios;

import com.cineplus.dto.PaginaDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.ResenaDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.TarjetaResena;
import com.cineplus.presentacion.Tema;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla F15: muro principal de Comunidad con las resenas mas recientes.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public class FrmComunidad extends JFrame {

    private final CoordinadorAplicacion app;

    private final JPanel pnlLista = new JPanel();
    private final JLabel lblResumen = new JLabel("Cargando reseñas de la comunidad...");
    private final JLabel lblPagina = new JLabel("Página 1 de 1");
    private final JButton btnAnterior = new JButton("Anterior");
    private final JButton btnSiguiente = new JButton("Siguiente");

    private List<ResenaDTO> resenasActuales = List.of();
    private String filtroTexto = "";
    private int paginaActual = 0;
    private int totalPaginas = 1;

    public FrmComunidad(CoordinadorAplicacion app) {
        this.app = app;

        setTitle("CinePlus · Comunidad");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(Tema.FONDO);

        // Cabecera superior con buscador y titulo
        JPanel cabecera = new JPanel(new BorderLayout(0, 12));
        cabecera.setOpaque(false);
        cabecera.add(new PanelEncabezado(this::filtrarLocalmente), BorderLayout.NORTH);

        JPanel bloqueTitulo = new JPanel(new BorderLayout(0, 4));
        bloqueTitulo.setOpaque(false);
        JLabel lblTitulo = new JLabel("Comunidad CinePlus");
        Tema.titulo(lblTitulo);
        lblResumen.setForeground(Tema.MUTED);
        bloqueTitulo.add(lblTitulo, BorderLayout.NORTH);
        bloqueTitulo.add(lblResumen, BorderLayout.SOUTH);
        cabecera.add(bloqueTitulo, BorderLayout.SOUTH);

        raiz.add(cabecera, BorderLayout.NORTH);

        // Centro: listado vertical de tarjetas de resena + paginacion
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);

        pnlLista.setOpaque(false);
        pnlLista.setLayout(new BoxLayout(pnlLista, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(pnlLista);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        centro.add(scroll, BorderLayout.CENTER);

        JPanel paginacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        paginacion.setOpaque(false);
        Tema.botonSecundario(btnAnterior);
        Tema.botonSecundario(btnSiguiente);
        lblPagina.setForeground(Tema.MUTED);

        btnAnterior.addActionListener(e -> {
            if (paginaActual > 0) {
                app.comunidad().cargarRecientes(this, paginaActual - 1);
            }
        });
        btnSiguiente.addActionListener(e -> {
            if (paginaActual + 1 < totalPaginas) {
                app.comunidad().cargarRecientes(this, paginaActual + 1);
            }
        });

        paginacion.add(btnAnterior);
        paginacion.add(lblPagina);
        paginacion.add(btnSiguiente);
        centro.add(paginacion, BorderLayout.SOUTH);

        raiz.add(centro, BorderLayout.CENTER);

        // Barra inferior de navegacion
        raiz.add(new PanelNavegacion(app, Seccion.COMUNIDAD), BorderLayout.SOUTH);

        setContentPane(raiz);
        Tema.preparar(this);

        SwingUtilities.invokeLater(() -> app.comunidad().cargarRecientes(this, 0));
    }

    public void ocupado(boolean cargando) {
        btnAnterior.setEnabled(!cargando && paginaActual > 0);
        btnSiguiente.setEnabled(!cargando && paginaActual + 1 < totalPaginas);
        setCursor(cargando ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void mostrarResenas(PaginaDTO<ResenaDTO> pagina) {
        this.resenasActuales = pagina.elementos();
        this.paginaActual = pagina.pagina();
        this.totalPaginas = Math.max(1, pagina.totalPaginas());

        lblResumen.setText(pagina.total() == 1
                ? "1 reseña compartida por la comunidad"
                : pagina.total() + " reseñas compartidas por la comunidad");
        lblPagina.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);
        btnAnterior.setEnabled(paginaActual > 0);
        btnSiguiente.setEnabled(paginaActual + 1 < totalPaginas);

        renderizarTarjetas();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "CinePlus · Comunidad", JOptionPane.ERROR_MESSAGE);
    }

    private void filtrarLocalmente(String texto) {
        this.filtroTexto = texto == null ? "" : texto.trim().toLowerCase();
        renderizarTarjetas();
    }

    private void renderizarTarjetas() {
        pnlLista.removeAll();
        List<ResenaDTO> visibles = resenasActuales.stream()
                .filter(r -> filtroTexto.isEmpty()
                        || (r.tituloPelicula() != null && r.tituloPelicula().toLowerCase().contains(filtroTexto))
                        || (r.autor() != null && r.autor().toLowerCase().contains(filtroTexto))
                        || (r.comentario() != null && r.comentario().toLowerCase().contains(filtroTexto)))
                .toList();

        if (visibles.isEmpty()) {
            JLabel vacio = new JLabel("No hay reseñas publicadas todavía. Selecciona una película en Inicio o Cartelera para escribir la primera.");
            vacio.setForeground(Tema.MUTED);
            vacio.setBorder(new EmptyBorder(24, 4, 24, 4));
            pnlLista.add(vacio);
        } else {
            for (ResenaDTO r : visibles) {
                pnlLista.add(new TarjetaResena(r, this::abrirResenasDePelicula));
                pnlLista.add(Box.createVerticalStrut(10));
            }
        }
        pnlLista.revalidate();
        pnlLista.repaint();
    }

    private void abrirResenasDePelicula(ResenaDTO r) {
        PeliculaDTO pelicula = new PeliculaDTO(
                r.idPelicula(),
                null,
                r.tituloPelicula(),
                "",
                "",
                0,
                null,
                "",
                "",
                null,
                true,
                null,
                null);
        app.comunidad().abrirResenas(pelicula);
    }
}