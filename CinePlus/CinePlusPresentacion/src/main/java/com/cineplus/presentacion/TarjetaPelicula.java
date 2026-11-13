package com.cineplus.presentacion;

import com.cineplus.dto.PeliculaDTO;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/**
 * Tarjeta de una pelicula: poster, titulo y debajo el anio con el director (o el genero).
 * Al hacer clic llama a alSeleccionar con la pelicula.
 *
 * @author luisf
 */
public final class TarjetaPelicula extends JPanel {

    private final LienzoPoster poster = new LienzoPoster();
    private final JLabel ficha;

    public TarjetaPelicula(
            PeliculaDTO pelicula,
            CoordinadorAplicacion coordinador,
            Consumer<PeliculaDTO> alSeleccionar) {
        setOpaque(false);
        setLayout(null);
        setToolTipText(pelicula.titulo());
        ficha = crearFicha(pelicula);
        add(poster);
        add(ficha);
        if (alSeleccionar != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evento) {
                    alSeleccionar.accept(pelicula);
                }
            });
        }
        cargarPoster(pelicula, coordinador);
    }

    @Override
    public void doLayout() {
        int altoDisponible = Math.max(0, getHeight() - 70);
        int altoPoster = Math.min((int) Math.round(getWidth() * 1.5), altoDisponible);
        int anchoPoster = Math.min(getWidth(), (int) Math.round(altoPoster / 1.5));
        poster.setBounds((getWidth() - anchoPoster) / 2, 0, anchoPoster, altoPoster);
        ficha.setBounds(0, altoPoster + 12, getWidth(), 58);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(155, 303);
    }

    private JLabel crearFicha(PeliculaDTO pelicula) {
        String anio = pelicula.fechaEstreno() != null
                ? String.valueOf(pelicula.fechaEstreno().getYear()) : "";
        String detalle;
        if (pelicula.director() != null && !pelicula.director().isBlank()) {
            detalle = anio.isEmpty() ? pelicula.director() : anio + " - " + pelicula.director();
        } else if (pelicula.genero() != null && !pelicula.genero().isBlank()) {
            detalle = anio.isEmpty() ? pelicula.genero() : anio + " · " + pelicula.genero();
        } else {
            detalle = anio;
        }
        String contenido
                = "<html><b>"
                + Tema.escapar(pelicula.titulo())
                + "</b><br><font color='#b3a4af'>"
                + Tema.escapar(detalle)
                + "</font></html>";
        JLabel ficha = new JLabel(contenido);
        ficha.setPreferredSize(new Dimension(145, 58));
        ficha.setForeground(Tema.TEXTO);
        ficha.setVerticalAlignment(SwingConstants.TOP);
        return ficha;
    }

    private void cargarPoster(PeliculaDTO pelicula, CoordinadorAplicacion coordinador) {
        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws IOException {
                byte[] datos = coordinador.poster(pelicula);
                return datos.length == 0 ? null : ImageIO.read(new ByteArrayInputStream(datos));
            }

            @Override
            protected void done() {
                try {
                    poster.imagen = get();
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException excepcion) {
                    // Se queda el lienzo "Poster no disponible" y se deja rastro en consola
                    System.err.println("No se pudo cargar el poster de \""
                            + pelicula.titulo() + "\": " + excepcion.getCause());
                }
                poster.repaint();
            }
        }.execute();
    }

    private static final class LienzoPoster extends JPanel {

        private BufferedImage imagen;

        private LienzoPoster() {
            setOpaque(false);
            setPreferredSize(new Dimension(155, 233));
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            super.paintComponent(graficos);
            Graphics2D lienzo = (Graphics2D) graficos.create();
            try {
                lienzo.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                lienzo.setColor(new Color(0x2e2331));
                lienzo.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                if (imagen == null) {
                    pintarEstadoVacio(lienzo);
                } else {
                    pintarImagen(lienzo);
                }
            } finally {
                lienzo.dispose();
            }
        }

        private void pintarImagen(Graphics2D lienzo) {
            lienzo.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            double escala
                    = Math.min(
                            (double) getWidth() / imagen.getWidth(),
                            (double) getHeight() / imagen.getHeight());
            int ancho = (int) (imagen.getWidth() * escala);
            int alto = (int) (imagen.getHeight() * escala);
            int x = (getWidth() - ancho) / 2;
            int y = (getHeight() - alto) / 2;
            lienzo.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            lienzo.drawImage(imagen, x, y, ancho, alto, null);
        }

        private void pintarEstadoVacio(Graphics2D lienzo) {
            lienzo.setColor(Tema.MUTED);
            lienzo.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lienzo.drawString("CinePLUS", 16, getHeight() / 2);
            lienzo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lienzo.drawString("Póster no disponible", 16, getHeight() / 2 + 23);
        }
    }
}
