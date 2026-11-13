package com.cineplus.presentacion;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;

import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class PresentadorCartelera {

    private static final int TARJETAS_VISIBLES = 6;

    private final CoordinadorAplicacion coordinador;
    private final JPanel contenedor;
    private final JTextField buscador;
    private final JLabel estado;
    private final JLabel mensajeVacio;
    private List<PeliculaDTO> peliculas = List.of();

    public PresentadorCartelera(
            CoordinadorAplicacion coordinador,
            JPanel contenedor,
            JTextField buscador,
            JLabel estado,
            JLabel mensajeVacio) {
        this.coordinador = coordinador;
        this.contenedor = contenedor;
        this.buscador = buscador;
        this.estado = estado;
        this.mensajeVacio = mensajeVacio;
        observarBusqueda();
    }

    public void cargar(JFrame ventana) {
        coordinador.cargarCartelera(ventana, this::mostrarCatalogo);
    }

    private void mostrarCatalogo(CarteleraDTO catalogo) {
        peliculas = catalogo.peliculas();
        estado.setText(
                catalogo.enVivo()
                        ? ""
                        : "Mostrando películas de muestra. No se pudo actualizar la cartelera.");
        estado.setVisible(!catalogo.enVivo());
        actualizarTarjetas();
    }

    private void observarBusqueda() {
        buscador.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(DocumentEvent evento) {
                                actualizarTarjetas();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent evento) {
                                actualizarTarjetas();
                            }

                            @Override
                            public void changedUpdate(DocumentEvent evento) {
                                actualizarTarjetas();
                            }
                        });
    }

    private void actualizarTarjetas() {
        List<PeliculaDTO> visibles = filtrar(peliculas, buscador.getText());
        contenedor.removeAll();
        for (PeliculaDTO pelicula : visibles) {
            contenedor.add(new TarjetaPelicula(pelicula, coordinador));
        }

        for (int indice = visibles.size(); indice < TARJETAS_VISIBLES; indice++) {
            JPanel espacio = new JPanel();
            espacio.setOpaque(false);
            contenedor.add(espacio);
        }
        mostrarEstadoVacio(visibles.isEmpty());
        contenedor.revalidate();
        contenedor.repaint();
    }

    private void mostrarEstadoVacio(boolean sinResultados) {
        if (!sinResultados) {
            mensajeVacio.setText(" ");
        } else if (buscador.getText().isBlank()) {
            mensajeVacio.setText("No hay películas disponibles por el momento.");
        } else {
            mensajeVacio.setText("No encontramos películas con ese nombre.");
        }
    }

    static List<PeliculaDTO> filtrar(List<PeliculaDTO> peliculas, String busqueda) {
        String criterio = busqueda.strip().toLowerCase(Locale.ROOT);
        return peliculas.stream()
                .filter(pelicula -> pelicula.titulo().toLowerCase(Locale.ROOT).contains(criterio))
                .limit(TARJETAS_VISIBLES)
                .toList();
    }
}
