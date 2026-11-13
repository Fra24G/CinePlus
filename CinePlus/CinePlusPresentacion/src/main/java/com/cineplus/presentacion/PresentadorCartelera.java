package com.cineplus.presentacion;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;

import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Llena el carrusel de la pantalla de inicio y lo filtra con el texto del buscador.
 * Ya no corta en 6: el carrusel decide cuantas peliculas se ven.
 *
 * @author luisf
 */
public final class PresentadorCartelera {

    private final CoordinadorAplicacion coordinador;
    private final CarruselPeliculas carrusel;
    private final JTextField buscador;
    private final JLabel estado;
    private final JLabel mensajeVacio;
    private List<PeliculaDTO> peliculas = List.of();

    public PresentadorCartelera(
            CoordinadorAplicacion coordinador,
            CarruselPeliculas carrusel,
            JTextField buscador,
            JLabel estado,
            JLabel mensajeVacio) {
        this.coordinador = coordinador;
        this.carrusel = carrusel;
        this.buscador = buscador;
        this.estado = estado;
        this.mensajeVacio = mensajeVacio;
        observarBusqueda();
    }

    public void cargar(JFrame ventana) {
        coordinador.cargarCartelera(ventana, this::mostrarCatalogo);
    }

    /** Guarda las peliculas y, si no estan en vivo, muestra el motivo real que dio el servicio. */
    private void mostrarCatalogo(CarteleraDTO catalogo) {
        peliculas = catalogo.peliculas();
        if (catalogo.enVivo()) {
            estado.setText("");
        } else {
            // estado() trae el motivo real, por ejemplo "TMDB rechazo la clave"
            estado.setText(catalogo.estado() == null || catalogo.estado().isBlank()
                    ? "Mostrando películas de muestra. No se pudo actualizar la cartelera."
                    : catalogo.estado());
        }
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
        carrusel.mostrar(visibles);
        mostrarEstadoVacio(visibles.isEmpty());
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

    /** Filtra por titulo sin distinguir mayusculas. Ya no limita a 6. */
    static List<PeliculaDTO> filtrar(List<PeliculaDTO> peliculas, String busqueda) {
        String criterio = busqueda.strip().toLowerCase(Locale.ROOT);
        return peliculas.stream()
                .filter(pelicula -> pelicula.titulo().toLowerCase(Locale.ROOT).contains(criterio))
                .toList();
    }
}
