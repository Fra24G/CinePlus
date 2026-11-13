package com.cineplus.presentacion;

import com.cineplus.dto.PeliculaDTO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Mosaico decorativo de posters de las pantallas de login y registro.
 *
 * @author luisf
 */
public final class PanelPosters extends JPanel {
    private static final int LIMITE_POSTERS = 3;
    private final List<BufferedImage> imagenes = new ArrayList<>();

    public PanelPosters(CoordinadorAplicacion coordinador) {
        setOpaque(false);
        setPreferredSize(new Dimension(380, 470));
        cargarImagenes(coordinador);
    }

    private void cargarImagenes(CoordinadorAplicacion coordinador) {
        new SwingWorker<List<BufferedImage>, Void>() {
            @Override
            protected List<BufferedImage> doInBackground() throws IOException {
                List<BufferedImage> resultado = new ArrayList<>();
                List<PeliculaDTO> peliculas = coordinador.catalogoDecorativo().peliculas();
                for (PeliculaDTO pelicula : peliculas.stream().limit(LIMITE_POSTERS).toList()) {
                    byte[] datos = coordinador.poster(pelicula);
                    if (datos.length == 0) {
                        continue;
                    }
                    BufferedImage imagen = ImageIO.read(new ByteArrayInputStream(datos));
                    if (imagen != null) {
                        resultado.add(imagen);
                    }
                }
                return resultado;
            }

            @Override
            protected void done() {
                try {
                    imagenes.addAll(get());
                } catch (InterruptedException excepcion) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException excepcion) {
                    // Se queda la marca CinePLUS y se deja rastro en consola
                    System.err.println("No se pudieron cargar los posters decorativos: "
                            + excepcion.getCause());
                }
                repaint();
            }
        }.execute();
    }

    @Override
    protected void paintComponent(Graphics graficos) {
        super.paintComponent(graficos);
        Graphics2D lienzo = (Graphics2D) graficos.create();
        try {
            lienzo.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            pintarFondo(lienzo);
            if (imagenes.isEmpty()) {
                pintarMarca(lienzo);
            } else {
                pintarPosters(lienzo);
            }
        } finally {
            lienzo.dispose();
        }
    }

    private void pintarFondo(Graphics2D lienzo) {
        lienzo.setPaint(
                new GradientPaint(0, 0, new Color(0x281725), getWidth(), getHeight(), Tema.FONDO));
        lienzo.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
    }

    private void pintarMarca(Graphics2D lienzo) {
        lienzo.setColor(Tema.MUTED);
        lienzo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lienzo.drawString("CinePLUS", 32, getHeight() / 2);
    }

    private void pintarPosters(Graphics2D lienzo) {
        int ancho = (int) (getWidth() * 0.60);
        int alto = (int) (ancho * 1.5);
        int margenSuperior = Math.max(25, (getHeight() - alto) / 2);

        for (int indice = imagenes.size() - 1; indice >= 0; indice--) {
            int x = 18 + indice * 48;
            int y = margenSuperior + indice * 22;
            lienzo.setColor(new Color(0, 0, 0, 75));
            lienzo.fillRoundRect(x + 7, y + 10, ancho, alto, 20, 20);

            Graphics2D poster = (Graphics2D) lienzo.create();
            try {
                poster.clip(new RoundRectangle2D.Double(x, y, ancho, alto, 18, 18));
                poster.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                poster.drawImage(imagenes.get(indice), x, y, ancho, alto, null);
            } finally {
                poster.dispose();
            }
        }
    }
}
