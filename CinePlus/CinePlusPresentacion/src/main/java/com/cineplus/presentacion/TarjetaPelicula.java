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
 * Tarjeta de una pelicula: poster redondeado con recorte tipo OBJECT_COVER y debajo el
 * texto centrado ("Titulo - Año" en la primera linea y el director en la segunda), tal
 * como en el Figma. Al hacer clic llama a alSeleccionar con la pelicula.
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
        int altoFicha = 68;
        int altoDisponible = Math.max(0, getHeight() - altoFicha);
        int altoPoster = Math.min((int) Math.round(getWidth() * 1.48), altoDisponible);
        int anchoPoster = Math.min(getWidth(), (int) Math.round(altoPoster / 1.48));
        poster.setBounds((getWidth() - anchoPoster) / 2, 0, anchoPoster, altoPoster);
        ficha.setBounds(0, altoPoster + 10, getWidth(), altoFicha - 10);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(190, 350);
    }

    private JLabel crearFicha(PeliculaDTO pelicula) {
        String anio = pelicula.fechaEstreno() != null
                ? String.valueOf(pelicula.fechaEstreno().getYear()) : "";
        String primeraLinea = anio.isEmpty()
                ? pelicula.titulo()
                : pelicula.titulo() + " - " + anio;
        String segundaLinea = "";
        if (pelicula.director() != null && !pelicula.director().isBlank()) {
            segundaLinea = pelicula.director();
        } else if (pelicula.genero() != null && !pelicula.genero().isBlank()) {
            segundaLinea = pelicula.genero();
        }
        String contenido = "<html><div style='text-align:center; width:135px; padding:0 4px'>"
                + "<b>" + Tema.escapar(primeraLinea) + "</b>"
                + (segundaLinea.isEmpty()
                        ? ""
                        : "<br><font color='#d4cdd1'><b>" + Tema.escapar(segundaLinea) + "</b></font>")
                + "</div></html>";
        JLabel etiqueta = new JLabel(contenido, SwingConstants.CENTER);
        etiqueta.setPreferredSize(new Dimension(180, 58));
        etiqueta.setFont(Tema.fuente(Font.BOLD, 14));
        etiqueta.setForeground(Tema.TEXTO);
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        etiqueta.setVerticalAlignment(SwingConstants.TOP);
        return etiqueta;
    }

    private void cargarPoster(PeliculaDTO pelicula, CoordinadorAplicacion coordinador) {
        if (coordinador == null) {
            return;
        }
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
            setPreferredSize(new Dimension(190, 280));
        }

        @Override
        protected void paintComponent(Graphics graficos) {
            super.paintComponent(graficos);
            Graphics2D lienzo = (Graphics2D) graficos.create();
            try {
                lienzo.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                lienzo.setColor(new Color(0x281c26));
                lienzo.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                if (imagen == null) {
                    pintarEstadoVacio(lienzo);
                } else {
                    pintarImagen(lienzo);
                }
            } finally {
                lienzo.dispose();
            }
        }

        /** OBJECT_COVER: rellena todo el rectangulo redondeado sin dejar franjas a los lados. */
        private void pintarImagen(Graphics2D lienzo) {
            lienzo.clip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 22, 22));
            double escala = Math.max(
                    (double) getWidth() / imagen.getWidth(),
                    (double) getHeight() / imagen.getHeight());
            int ancho = (int) Math.ceil(imagen.getWidth() * escala);
            int alto = (int) Math.ceil(imagen.getHeight() * escala);
            int x = (getWidth() - ancho) / 2;
            int y = (getHeight() - alto) / 2;
            lienzo.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            lienzo.drawImage(imagen, x, y, ancho, alto, null);
        }

        private void pintarEstadoVacio(Graphics2D lienzo) {
            lienzo.setColor(Tema.MUTED);
            lienzo.setFont(Tema.fuenteLogo(15));
            lienzo.drawString("CinePLUS", 18, getHeight() / 2);
            lienzo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lienzo.drawString("Póster no disponible", 18, getHeight() / 2 + 22);
        }
    }
}
