package com.cineplus.presentacion;

import com.cineplus.dto.PeliculaDTO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JPanel;

/**
 * Carrusel de peliculas de la pantalla de inicio. Muestra 4 tarjetas completas en el
 * centro y una tarjeta parcial y atenuada cortada en cada extremo (izquierdo y derecho),
 * como en el Figma. Avanza al hacer clic en las tarjetas parciales de los extremos o con
 * la rueda del mouse y es circular.
 *
 * @author luisf
 */
public final class CarruselPeliculas extends JPanel {

    private static final int VISIBLES = 4;
    private static final int SEPARACION = 26;
    private static final int ANCHO_MAXIMO = 230;

    private final CoordinadorAplicacion coordinador;
    private final Consumer<PeliculaDTO> alSeleccionar;
    private final Pista pista = new Pista();

    /** Tarjetas ya creadas, para no volver a cargar el poster en cada movimiento. */
    private final Map<Integer, TarjetaPelicula> completas = new HashMap<>();
    private final Map<Integer, Atenuada> parciales = new HashMap<>();

    private List<PeliculaDTO> peliculas = List.of();
    private int posicion;

    public CarruselPeliculas(CoordinadorAplicacion coordinador, Consumer<PeliculaDTO> alSeleccionar) {
        super(new BorderLayout());
        this.coordinador = coordinador;
        this.alSeleccionar = alSeleccionar;
        setOpaque(false);
        add(pista, BorderLayout.CENTER);
        addMouseWheelListener(e -> {
            if (e.getWheelRotation() > 0) {
                siguiente();
            } else if (e.getWheelRotation() < 0) {
                anterior();
            }
        });
    }

    /** Recibe la lista completa (sin limite) y regresa al inicio. */
    public void mostrar(List<PeliculaDTO> peliculas) {
        this.peliculas = List.copyOf(peliculas);
        completas.clear();
        parciales.clear();
        posicion = 0;
        reconstruir();
    }

    /** Avanza una posicion; al llegar al final regresa al inicio. */
    public void siguiente() {
        if (peliculas.size() > VISIBLES) {
            posicion = (posicion + 1) % peliculas.size();
            reconstruir();
        }
    }

    /** Retrocede una posicion; si esta al inicio va al final. */
    public void anterior() {
        if (peliculas.size() > VISIBLES) {
            posicion = (posicion - 1 + peliculas.size()) % peliculas.size();
            reconstruir();
        }
    }

    /** Quita las tarjetas y pone las 4 completas desde la posicion, mas una parcial a cada lado. */
    private void reconstruir() {
        pista.removeAll();
        pista.izquierda = null;
        pista.derecha = null;
        pista.centro.clear();

        int total = peliculas.size();
        for (int i = 0; i < Math.min(VISIBLES, total); i++) {
            int indice = (posicion + i) % total;
            TarjetaPelicula tarjeta = completas.computeIfAbsent(indice,
                    k -> new TarjetaPelicula(peliculas.get(k), coordinador, alSeleccionar));
            pista.centro.add(tarjeta);
            pista.add(tarjeta);
        }
        // Con 5 peliculas solo cabe la parcial izquierda; con 6 o mas, las dos parciales
        if (total > VISIBLES) {
            pista.izquierda = parcial((posicion - 1 + total) % total, p -> anterior());
            pista.add(pista.izquierda);
        }
        if (total > VISIBLES + 1) {
            pista.derecha = parcial((posicion + VISIBLES) % total, p -> siguiente());
            pista.add(pista.derecha);
        }
        pista.revalidate();
        pista.repaint();
    }

    private Atenuada parcial(int indice, Consumer<PeliculaDTO> alClic) {
        return parciales.computeIfAbsent(indice,
                k -> new Atenuada(new TarjetaPelicula(peliculas.get(k), coordinador, alClic)));
    }

    /** Area central: acomoda las 4 tarjetas completas al centro y corta las de los bordes. */
    private static final class Pista extends JPanel {

        private final List<Component> centro = new ArrayList<>();
        private Component izquierda;
        private Component derecha;

        private Pista() {
            super(null);
            setOpaque(false);
        }

        @Override
        public void doLayout() {
            int ancho = getWidth();
            int alto = getHeight();
            if (centro.isEmpty() || ancho <= 0) {
                return;
            }
            // Reserva ~0.55 de tarjeta a cada lado para que las parciales asomen cortadas como en Figma
            double columnas = Math.max(VISIBLES, centro.size())
                    + (izquierda != null || derecha != null ? 1.05 : 0);
            int tarjeta = Math.min(
                    ANCHO_MAXIMO,
                    (int) Math.round((ancho - SEPARACION * (VISIBLES + 1)) / columnas));
            int usado = centro.size() * tarjeta + (centro.size() - 1) * SEPARACION;
            int inicio = (ancho - usado) / 2;
            int x = inicio;
            for (Component c : centro) {
                c.setBounds(x, 0, tarjeta, alto);
                x += tarjeta + SEPARACION;
            }
            int desfaseLateral = 14;
            int altoLateral = Math.max(0, alto - desfaseLateral);
            if (izquierda != null) {
                izquierda.setBounds(inicio - SEPARACION - tarjeta, desfaseLateral, tarjeta, altoLateral);
            }
            if (derecha != null) {
                derecha.setBounds(inicio + usado + SEPARACION, desfaseLateral, tarjeta, altoLateral);
            }
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics g2 = g.create();
            try {
                g2.clipRect(0, 0, getWidth(), getHeight());
                super.paintChildren(g2);
            } finally {
                g2.dispose();
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1020, 390);
        }
    }

    /** Tarjeta de borde: se pinta normal y encima un velo oscuro para atenuarla como en el Figma. */
    private static final class Atenuada extends JPanel {

        private static final Color VELO = new Color(
                Tema.FONDO.getRed(), Tema.FONDO.getGreen(), Tema.FONDO.getBlue(), 165);

        private Atenuada(TarjetaPelicula tarjeta) {
            super(new BorderLayout());
            setOpaque(false);
            setToolTipText("Desplazar carrusel");
            add(tarjeta, BorderLayout.CENTER);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(VELO);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
