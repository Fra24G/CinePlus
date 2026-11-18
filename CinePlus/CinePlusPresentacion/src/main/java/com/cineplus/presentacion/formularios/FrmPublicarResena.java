/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.presentacion.formularios;

import com.cineplus.dto.NuevaResenaDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.presentacion.CoordinadorAplicacion;
import com.cineplus.presentacion.PanelEncabezado;
import com.cineplus.presentacion.PanelNavegacion;
import com.cineplus.presentacion.Seccion;
import com.cineplus.presentacion.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Pantalla F16: formulario para redactar y publicar una nueva resena.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public class FrmPublicarResena extends JFrame {

    private static final Color ORO_ESTRELLA = new Color(0xe5a93c);

    private final CoordinadorAplicacion app;
    private final PeliculaDTO pelicula;

    private final JButton[] btnEstrellas = new JButton[5];
    private final JLabel lblCalificacion = new JLabel("Calificación: 5 / 5");
    private final JTextArea txtComentario = new JTextArea(6, 40);
    private final JLabel lblContador = new JLabel("0 / 500 caracteres (mínimo 10)");
    private final JLabel lblError = new JLabel(" ");
    private final JButton btnCancelar = new JButton("CANCELAR");
    private final JButton btnPublicar = new JButton("PUBLICAR RESEÑA");

    private int calificacionSeleccionada = 5;

    public FrmPublicarResena(CoordinadorAplicacion app, PeliculaDTO pelicula) {
        this.app = app;
        this.pelicula = pelicula;

        setTitle("CinePlus · Publicar Reseña");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 18));
        raiz.setBackground(Tema.FONDO);

        raiz.add(new PanelEncabezado(null), BorderLayout.NORTH);

        // Tarjeta centrada del formulario
        JPanel contenedorCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        contenedorCentro.setOpaque(false);

        JPanel tarjeta = new JPanel();
        Tema.tarjeta(tarjeta);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setPreferredSize(new Dimension(640, 440));

        JLabel lblTitulo = new JLabel("Tu Reseña:");
        Tema.titulo(lblTitulo);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(4));

        String nombrePelicula = pelicula != null ? pelicula.titulo() : "Película seleccionada";
        JLabel lblPelicula = new JLabel("Película: " + nombrePelicula);
        lblPelicula.setForeground(Tema.MUTED);
        lblPelicula.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPelicula.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(lblPelicula);
        tarjeta.add(Box.createVerticalStrut(16));

        // Selector de estrellas
        JPanel filaEstrellas = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filaEstrellas.setOpaque(false);
        filaEstrellas.setAlignmentX(LEFT_ALIGNMENT);
        filaEstrellas.setMaximumSize(new Dimension(600, 44));

        for (int i = 0; i < 5; i++) {
            final int valor = i + 1;
            JButton estrella = new JButton("★");
            estrella.setFont(new Font("Segoe UI", Font.BOLD, 24));
            estrella.setFocusPainted(false);
            estrella.setBorderPainted(false);
            estrella.setContentAreaFilled(false);
            estrella.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            estrella.addActionListener(e -> seleccionarEstrellas(valor));
            btnEstrellas[i] = estrella;
            filaEstrellas.add(estrella);
        }
        lblCalificacion.setForeground(Tema.TEXTO);
        lblCalificacion.setBorder(new EmptyBorder(0, 10, 0, 0));
        filaEstrellas.add(lblCalificacion);

        tarjeta.add(filaEstrellas);
        tarjeta.add(Box.createVerticalStrut(14));

        JLabel lblInstruccion = new JLabel("Escribe tu opinión sobre la película:");
        lblInstruccion.setForeground(Tema.TEXTO);
        lblInstruccion.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(lblInstruccion);
        tarjeta.add(Box.createVerticalStrut(6));

        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        txtComentario.setBackground(Tema.CAMPO);
        txtComentario.setForeground(Tema.TEXTO);
        txtComentario.setCaretColor(Tema.TEXTO);
        txtComentario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtComentario.setBorder(new EmptyBorder(10, 12, 10, 12));
        txtComentario.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarContador();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarContador();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarContador();
            }
        });

        JScrollPane scrollComentario = new JScrollPane(txtComentario);
        scrollComentario.setAlignmentX(LEFT_ALIGNMENT);
        scrollComentario.setMaximumSize(new Dimension(600, 150));
        tarjeta.add(scrollComentario);
        tarjeta.add(Box.createVerticalStrut(6));

        lblContador.setForeground(Tema.MUTED);
        lblContador.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblContador.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(lblContador);
        tarjeta.add(Box.createVerticalStrut(8));

        lblError.setForeground(Tema.ERROR);
        lblError.setAlignmentX(LEFT_ALIGNMENT);
        tarjeta.add(lblError);
        tarjeta.add(Box.createVerticalStrut(14));

        // Botones inferiores
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filaBotones.setOpaque(false);
        filaBotones.setAlignmentX(LEFT_ALIGNMENT);
        filaBotones.setMaximumSize(new Dimension(600, 42));

        Tema.botonSecundario(btnCancelar);
        Tema.botonPrimario(btnPublicar);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPublicar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCancelar.addActionListener(e -> {
            if (pelicula != null) {
                app.comunidad().abrirResenas(pelicula);
            } else {
                app.comunidad().abrir();
            }
        });
        btnPublicar.addActionListener(e -> enviarResena());

        filaBotones.add(btnCancelar);
        filaBotones.add(btnPublicar);
        tarjeta.add(filaBotones);

        contenedorCentro.add(tarjeta);
        raiz.add(contenedorCentro, BorderLayout.CENTER);
        raiz.add(new PanelNavegacion(app, Seccion.COMUNIDAD), BorderLayout.SOUTH);

        setContentPane(raiz);
        seleccionarEstrellas(5);
        Tema.preparar(this);
    }

    private void seleccionarEstrellas(int valor) {
        this.calificacionSeleccionada = valor;
        for (int i = 0; i < 5; i++) {
            btnEstrellas[i].setForeground(i < valor ? ORO_ESTRELLA : Tema.MUTED);
        }
        lblCalificacion.setText("Calificación: " + valor + " / 5");
    }

    private void actualizarContador() {
        int longitud = txtComentario.getText().trim().length();
        lblContador.setText(longitud + " / 500 caracteres (mínimo 10)");
        lblContador.setForeground(longitud > 500 ? Tema.ERROR : Tema.MUTED);
        Tema.marcarError(txtComentario, false);
        lblError.setText(" ");
    }

    private void enviarResena() {
        if (pelicula == null) {
            mostrarError("No se seleccionó ninguna película para reseñar.");
            return;
        }
        NuevaResenaDTO dto = new NuevaResenaDTO(
                pelicula.idPelicula(),
                pelicula.idExterno(),
                calificacionSeleccionada,
                txtComentario.getText());
        app.comunidad().publicar(this, dto);
    }

    public void ocupado(boolean cargando) {
        btnPublicar.setEnabled(!cargando);
        btnCancelar.setEnabled(!cargando);
        txtComentario.setEnabled(!cargando);
        setCursor(cargando ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        Tema.marcarError(txtComentario, true);
    }
}
