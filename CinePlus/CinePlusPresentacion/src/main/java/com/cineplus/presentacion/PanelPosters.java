package com.cineplus.presentacion;

import com.cineplus.dto.PeliculaDTO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
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
 * Abanico decorativo de 3 posters superpuestos con ligera rotacion y sombra para las
 * pantallas de login y registro, tal como en el Figma.
 *
 * @author luisf
 */
public final class PanelPosters extends JPanel {

    private static final int LIMITE_POSTERS = 3;
    private static final double[] ANGULOS_GRADOS = {-2.0, 1.0, 2.2};
    private static final int[] DESFASE_Y = {6, 22, 52};
    private final List<BufferedImage> imagenes = new ArrayList<>();

    public PanelPosters(CoordinadorAplicacion coordinador) {
        setOpaque(false);
        setPreferredSize(new Dimension(460, 520));
        if (coordinador != null) {
            cargarImagenes(coordinador);
        }
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
            if (imagenes.isEmpty()) {
                pintarMarca(lienzo);
            } else {
                pintarPosters(lienzo);
            }
        } finally {
            lienzo.dispose();
        }
    }

    private void pintarMarca(Graphics2D lienzo) {
        lienzo.setColor(Tema.MUTED);
        lienzo.setFont(Tema.fuenteLogo(28));
        lienzo.drawString("CinePLUS", 36, getHeight() / 2);
    }

    /**
     * Dibuja del poster del fondo (derecha) al del frente (izquierda), con rotacion sutil,
     * sombra suave y recorte OBJECT_COVER como en el Figma.
     */
    private void pintarPosters(Graphics2D lienzo) {
        int altoMax = Math.max(260, getHeight() - 24);
        int ancho = Math.min((int) (getWidth() * 0.68), (int) (altoMax / 1.44));
        int alto = (int) (ancho * 1.44);
        int pasoX = Math.max(48, (int) (ancho * 0.31));
        int baseY = Math.max(4, (getHeight() - alto - 44) / 2);

        for (int indice = imagenes.size() - 1; indice >= 0; indice--) {
            int x = 8 + indice * pasoX;
            int dy = indice < DESFASE_Y.length ? DESFASE_Y[indice] : indice * 20;
            int y = baseY + dy;
            double angulo = Math.toRadians(
                    indice < ANGULOS_GRADOS.length ? ANGULOS_GRADOS[indice] : 0);

            Graphics2D g2 = (Graphics2D) lienzo.create();
            try {
                AffineTransform original = g2.getTransform();
                g2.rotate(angulo, x + ancho / 2.0, y + alto / 2.0);

                // Sombra suave debajo de cada poster
                g2.setColor(new Color(0, 0, 0, 115));
                g2.fillRoundRect(x + 8, y + 12, ancho, alto, 26, 26);

                BufferedImage img = imagenes.get(indice);
                g2.clip(new RoundRectangle2D.Double(x, y, ancho, alto, 24, 24));
                double escala = Math.max(
                        (double) ancho / img.getWidth(),
                        (double) alto / img.getHeight());
                int imgAncho = (int) Math.ceil(img.getWidth() * escala);
                int imgAlto = (int) Math.ceil(img.getHeight() * escala);
                int imgX = x + (ancho - imgAncho) / 2;
                int imgY = y + (alto - imgAlto) / 2;
                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(img, imgX, imgY, imgAncho, imgAlto, null);

                // Oscurece ligeramente los posters de atras para dar profundidad
                if (indice > 0) {
                    g2.setColor(new Color(12, 6, 10, indice == 1 ? 45 : 85));
                    g2.fillRect(x, y, ancho, alto);
                }
                g2.setTransform(original);
            } finally {
                g2.dispose();
            }
        }
    }
}
