/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cineplus.presentacion;

import com.cineplus.dto.ResenaDTO;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Tarjeta reutilizable para mostrar una resena en el muro de Comunidad.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public final class TarjetaResena extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TarjetaResena(ResenaDTO resena, Consumer<ResenaDTO> alSeleccionarPelicula) {
        super(new BorderLayout(0, 8));
        Tema.tarjeta(this);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Encabezado: titulo de pelicula + estrellas a la izquierda, fecha a la derecha
        JPanel filaSuperior = new JPanel(new BorderLayout(12, 0));
        filaSuperior.setOpaque(false);

        int puntos = Math.max(0, Math.min(5, resena.calificacion()));
        String estrellas = "★".repeat(puntos) + "☆".repeat(5 - puntos);

        JLabel lblPelicula = new JLabel("<html><b>" + Tema.escapar(resena.tituloPelicula())
                + "</b> &nbsp; <font color='#e5a93c'>" + estrellas + "</font></html>");
        lblPelicula.setForeground(Tema.TEXTO);
        lblPelicula.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        String fechaStr = resena.fecha() != null ? resena.fecha().format(FORMATO_FECHA) : "";
        JLabel lblFecha = new JLabel(fechaStr);
        lblFecha.setForeground(Tema.MUTED);
        lblFecha.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        filaSuperior.add(lblPelicula, BorderLayout.WEST);
        filaSuperior.add(lblFecha, BorderLayout.EAST);

        // Subtitulo con el autor y cuerpo del comentario
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel lblAutor = new JLabel("Publicado por " + resena.autor());
        lblAutor.setForeground(Tema.MUTED);
        lblAutor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(lblAutor);
        centro.add(Box.createVerticalStrut(6));

        JTextArea txtComentario = new JTextArea(resena.comentario());
        txtComentario.setEditable(false);
        txtComentario.setLineWrap(true);
        txtComentario.setWrapStyleWord(true);
        txtComentario.setOpaque(false);
        txtComentario.setForeground(Tema.TEXTO);
        txtComentario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtComentario.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(txtComentario);

        add(filaSuperior, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        // Accion para ir a todas las resenas de esa pelicula
        if (alSeleccionarPelicula != null && resena.idPelicula() != null) {
            JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            pie.setOpaque(false);

            JButton btnVerMas = new JButton("Ver reseñas de esta película →");
            btnVerMas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnVerMas.setFocusable(false);
            Tema.botonSecundario(btnVerMas);
            btnVerMas.addActionListener(e -> alSeleccionarPelicula.accept(resena));

            pie.add(btnVerMas);
            add(pie, BorderLayout.SOUTH);

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    alSeleccionarPelicula.accept(resena);
                }
            });
        }
    }
}
