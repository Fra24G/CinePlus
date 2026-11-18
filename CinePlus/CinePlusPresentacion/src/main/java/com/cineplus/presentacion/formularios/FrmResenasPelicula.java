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
import com.cineplus.presentacion.TarjetaPelicula;
import com.cineplus.presentacion.Tema;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla F16: detalle de una pelicula y listado paginado de sus resenas.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public class FrmResenasPelicula extends JFrame {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CoordinadorAplicacion app;
    private final PeliculaDTO pelicula;

    private final JPanel pnlLista = new JPanel();
    private final JLabel lblResumen = new JLabel("Cargando reseñas...");
    private final JLabel lblPagina = new JLabel("Página 1 de 1");
    private final JButton btnAnterior = new JButton("Anterior");
    private final JButton btnSiguiente = new JButton("Siguiente");
    private final JButton btnEscribir = new JButton("ESCRIBIR RESEÑA");

    private List<ResenaDTO> resenasActuales = List.of();
    private String filtroTexto = "";
    private int paginaActual = 0;
    private int totalPaginas = 1;

    public FrmResenasPelicula(CoordinadorAplicacion app, PeliculaDTO pelicula) {
        this.app = app;
        this.pelicula = pelicula;

        setTitle("CinePlus · Reseñas de " + (pelicula != null ? pelicula.titulo() : "Película"));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(Tema.FONDO);

        // Cabecera superior
        JPanel cabecera = new JPanel(new BorderLayout(0, 10));
        cabecera.setOpaque(false);
        cabecera.add(new PanelEncabezado(this::filtrarLocalmente), BorderLayout.NORTH);

        JPanel barraVolver = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraVolver.setOpaque(false);
        JButton btnVolver = new JButton("← Volver a Comunidad");
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Tema.botonSecundario(btnVolver);
        btnVolver.addActionListener(e -> app.comunidad().abrir());
        barraVolver.add(btnVolver);
        cabecera.add(barraVolver, BorderLayout.SOUTH);

        raiz.add(cabecera, BorderLayout.NORTH);

        // Cuerpo principal: ficha de pelicula a la izquierda, resenas a la derecha
        JPanel centro = new JPanel(new BorderLayout(24, 0));
        centro.setOpaque(false);
        centro.add(crearPanelPelicula(), BorderLayout.WEST);
        centro.add(crearPanelResenas(), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        // Barra inferior de navegacion
        raiz.add(new PanelNavegacion(app, Seccion.COMUNIDAD), BorderLayout.SOUTH);

        setContentPane(raiz);
        Tema.preparar(this);

        SwingUtilities.invokeLater(() -> app.comunidad().cargarResenas(this, pelicula, 0));
    }

    private JPanel crearPanelPelicula() {
        JPanel lateral = new JPanel();
        lateral.setOpaque(false);
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setPreferredSize(new Dimension(250, 0));

        if (pelicula != null) {
            TarjetaPelicula tarjeta = new TarjetaPelicula(pelicula, app, null);
            tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
            lateral.add(tarjeta);
            lateral.add(Box.createVerticalStrut(10));

            String meta = construirMetadatos(pelicula);
            if (!meta.isBlank()) {
                JLabel lblMeta = new JLabel(meta);
                lblMeta.setForeground(Tema.MUTED);
                lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lblMeta.setAlignmentX(Component.LEFT_ALIGNMENT);
                lateral.add(lblMeta);
                lateral.add(Box.createVerticalStrut(10));
            }

            if (pelicula.sinopsis() != null && !pelicula.sinopsis().isBlank()) {
                JTextArea txtSinopsis = new JTextArea(pelicula.sinopsis());
                txtSinopsis.setEditable(false);
                txtSinopsis.setLineWrap(true);
                txtSinopsis.setWrapStyleWord(true);
                txtSinopsis.setOpaque(false);
                txtSinopsis.setForeground(Tema.MUTED);
                txtSinopsis.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                txtSinopsis.setAlignmentX(Component.LEFT_ALIGNMENT);
                txtSinopsis.setMaximumSize(new Dimension(250, 140));
                lateral.add(txtSinopsis);
                lateral.add(Box.createVerticalStrut(14));
            }
        }

        Tema.botonPrimario(btnEscribir);
        btnEscribir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEscribir.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnEscribir.setMaximumSize(new Dimension(250, 40));
        btnEscribir.addActionListener(e -> app.comunidad().abrirPublicar(pelicula));
        lateral.add(btnEscribir);

        return lateral;
    }

    private JPanel crearPanelResenas() {
        JPanel derecho = new JPanel(new BorderLayout(0, 12));
        derecho.setOpaque(false);

        JPanel encabezadoLista = new JPanel(new BorderLayout());
        encabezadoLista.setOpaque(false);
        JLabel lblTitulo = new JLabel("Reseñas de " + (pelicula != null ? pelicula.titulo() : ""));
        Tema.titulo(lblTitulo);
        lblResumen.setForeground(Tema.MUTED);
        encabezadoLista.add(lblTitulo, BorderLayout.NORTH);
        encabezadoLista.add(lblResumen, BorderLayout.SOUTH);
        derecho.add(encabezadoLista, BorderLayout.NORTH);

        pnlLista.setOpaque(false);
        pnlLista.setLayout(new BoxLayout(pnlLista, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(pnlLista);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        derecho.add(scroll, BorderLayout.CENTER);

        // Paginacion
        JPanel paginacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        paginacion.setOpaque(false);
        Tema.botonSecundario(btnAnterior);
        Tema.botonSecundario(btnSiguiente);
        lblPagina.setForeground(Tema.MUTED);

        btnAnterior.addActionListener(e -> {
            if (paginaActual > 0) {
                app.comunidad().cargarResenas(this, pelicula, paginaActual - 1);
            }
        });
        btnSiguiente.addActionListener(e -> {
            if (paginaActual + 1 < totalPaginas) {
                app.comunidad().cargarResenas(this, pelicula, paginaActual + 1);
            }
        });

        paginacion.add(btnAnterior);
        paginacion.add(lblPagina);
        paginacion.add(btnSiguiente);
        derecho.add(paginacion, BorderLayout.SOUTH);

        return derecho;
    }

    public void ocupado(boolean cargando) {
        btnEscribir.setEnabled(!cargando);
        btnAnterior.setEnabled(!cargando && paginaActual > 0);
        btnSiguiente.setEnabled(!cargando && paginaActual + 1 < totalPaginas);
        setCursor(cargando ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void mostrarResenas(PaginaDTO<ResenaDTO> pagina) {
        this.resenasActuales = pagina.elementos();
        this.paginaActual = pagina.pagina();
        this.totalPaginas = Math.max(1, pagina.totalPaginas());

        lblResumen.setText(pagina.total() == 1
                ? "1 reseña publicada"
                : pagina.total() + " reseñas publicadas");
        lblPagina.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);
        btnAnterior.setEnabled(paginaActual > 0);
        btnSiguiente.setEnabled(paginaActual + 1 < totalPaginas);

        renderizarTarjetas();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "CinePlus", JOptionPane.ERROR_MESSAGE);
    }

    private void filtrarLocalmente(String texto) {
        this.filtroTexto = texto == null ? "" : texto.trim().toLowerCase();
        renderizarTarjetas();
    }

    private void renderizarTarjetas() {
        pnlLista.removeAll();
        List<ResenaDTO> visibles = resenasActuales.stream()
                .filter(r -> filtroTexto.isEmpty()
                        || (r.autor() != null && r.autor().toLowerCase().contains(filtroTexto))
                        || (r.comentario() != null && r.comentario().toLowerCase().contains(filtroTexto)))
                .toList();

        if (visibles.isEmpty()) {
            JLabel vacio = new JLabel("Aún no hay reseñas para mostrar. ¡Sé el primero en opinar!");
            vacio.setForeground(Tema.MUTED);
            vacio.setBorder(new EmptyBorder(24, 4, 24, 4));
            pnlLista.add(vacio);
        } else {
            for (ResenaDTO r : visibles) {
                pnlLista.add(crearTarjetaResena(r));
                pnlLista.add(Box.createVerticalStrut(10));
            }
        }
        pnlLista.revalidate();
        pnlLista.repaint();
    }

    private JPanel crearTarjetaResena(ResenaDTO r) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        Tema.tarjeta(card);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        String estrellas = "★".repeat(Math.max(0, r.calificacion()))
                + "☆".repeat(Math.max(0, 5 - r.calificacion()));
        JLabel lblAutor = new JLabel("<html><b>" + Tema.escapar(r.autor())
                + "</b> &nbsp; <font color='#e5a93c'>" + estrellas + "</font></html>");
        lblAutor.setForeground(Tema.TEXTO);

        String fechaStr = r.fecha() != null ? r.fecha().format(FORMATO_FECHA) : "";
        JLabel lblFecha = new JLabel(fechaStr);
        lblFecha.setForeground(Tema.MUTED);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        top.add(lblAutor, BorderLayout.WEST);
        top.add(lblFecha, BorderLayout.EAST);

        JTextArea txt = new JTextArea(r.comentario());
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setOpaque(false);
        txt.setForeground(Tema.TEXTO);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        card.add(top, BorderLayout.NORTH);
        card.add(txt, BorderLayout.CENTER);
        return card;
    }

    private static String construirMetadatos(PeliculaDTO p) {
        StringBuilder sb = new StringBuilder();
        if (p.clasificacion() != null && !p.clasificacion().isBlank()) {
            sb.append(p.clasificacion());
        }
        if (p.duracionMinutos() > 0) {
            if (!sb.isEmpty()) sb.append(" · ");
            sb.append(p.duracionMinutos()).append(" min");
        }
        if (p.genero() != null && !p.genero().isBlank()) {
            if (!sb.isEmpty()) sb.append(" · ");
            sb.append(p.genero());
        }
        return sb.toString();
    }
}
