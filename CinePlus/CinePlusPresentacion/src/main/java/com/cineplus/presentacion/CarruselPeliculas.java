package com.cineplus.presentacion;

import com.cineplus.dto.PeliculaDTO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Carrusel de peliculas de la pantalla de inicio. Muestra 4 tarjetas completas y una
 * parcial y atenuada a cada lado, como en el Figma. Avanza con las flechas o con la rueda
 * del mouse y es circular. Reemplaza la cuadricula fija de 6 tarjetas.
 *
 * @author luisf
 */
public final class CarruselPeliculas extends JPanel {

    private static final int VISIBLES = 4;
    private static final int SEPARACION = 18;
    private static final int ANCHO_MAXIMO = 210;

    private final CoordinadorAplicacion coordinador;
    private final Consumer<PeliculaDTO> alSeleccionar;
    private final JButton btnAnterior = flecha("‹", "Anterior");
    private final JButton btnSiguiente = flecha("›", "Siguiente");
    private final Pista pista = new Pista();

    /** Tarjetas ya creadas, para no volver a cargar el poster en cada movimiento. */
    private final Map<Integer, TarjetaPelicula> completas = new HashMap<>();
    private final Map<Integer, Atenuada> parciales = new HashMap<>();

    private List<PeliculaDTO> peliculas = List.of();
    private int posicion;

    public CarruselPeliculas(CoordinadorAplicacion coordinador, Consumer<PeliculaDTO> alSeleccionar) {
        super(new BorderLayout(10, 0));
        this.coordinador = coordinador;
        this.alSeleccionar = alSeleccionar;
        setOpaque(false);
        btnAnterior.addActionListener(e -> anterior());
        btnSiguiente.addActionListener(e -> siguiente());
        add(centrada(btnAnterior), BorderLayout.WEST);
        add(pista, BorderLayout.CENTER);
        add(centrada(btnSiguiente), BorderLayout.EAST);
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
        boolean desplazable = total > VISIBLES;
        btnAnterior.setVisible(desplazable);
        btnSiguiente.setVisible(desplazable);

        for (int i = 0; i < Math.min(VISIBLES, total); i++) {
            int indice = (posicion + i) % total;
            TarjetaPelicula tarjeta = completas.computeIfAbsent(indice,
                    k -> new TarjetaPelicula(peliculas.get(k), coordinador, alSeleccionar));
            pista.centro.add(tarjeta);
            pista.add(tarjeta);
        }
        // Con 5 peliculas solo cabe la parcial izquierda; con 6 o mas, las dos
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
        Atenuada parcial = parciales.computeIfAbsent(indice,
                k -> new Atenuada(new TarjetaPelicula(peliculas.get(k), coordinador, alClic)));
        return parcial;
    }

    /** Pone la flecha a media altura sin estirarla. */
    private static JPanel centrada(JButton flecha) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.add(flecha);
        return panel;
    }

    private static JButton flecha(String texto, String ayuda) {
        JButton boton = new JButton(texto);
        boton.setToolTipText(ayuda);
        boton.getAccessibleContext().setAccessibleName(ayuda);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.setFocusable(false);
        boton.setPreferredSize(new Dimension(44, 64));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.putClientProperty("JButton.buttonType", "roundRect");
        Tema.botonSecundario(boton);
        return boton;
    }

    /** Area central: acomoda las tarjetas a mano para que las de los bordes queden cortadas. */
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
            int lugares = centro.size() + (izquierda != null || derecha != null ? 1 : 0);
            if (centro.isEmpty() || ancho <= 0) {
                return;
            }
            int columnas = Math.max(VISIBLES, lugares);
            int tarjeta = Math.min(ANCHO_MAXIMO, (ancho - SEPARACION * columnas) / columnas);
            int usado = centro.size() * tarjeta + (centro.size() - 1) * SEPARACION;
            int x = (ancho - usado) / 2;
            for (Component c : centro) {
                c.setBounds(x, 0, tarjeta, alto);
                x += tarjeta + SEPARACION;
            }
            int inicio = (ancho - usado) / 2;
            if (izquierda != null) {
                izquierda.setBounds(inicio - SEPARACION - tarjeta, 0, tarjeta, alto);
            }
            if (derecha != null) {
                derecha.setBounds(inicio + usado + SEPARACION, 0, tarjeta, alto);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(900, 320);
        }
    }

    /** Tarjeta de borde: se pinta normal y encima un velo del color de fondo. */
    private static final class Atenuada extends JPanel {

        private static final Color VELO = new Color(
                Tema.FONDO.getRed(), Tema.FONDO.getGreen(), Tema.FONDO.getBlue(), 160);

        private Atenuada(TarjetaPelicula tarjeta) {
            super(new BorderLayout());
            setOpaque(false);
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
