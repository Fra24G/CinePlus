
package com.cineplus.negocio.bo;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.servicio.IServicioPeliculas;
import java.util.List;

/**
 * Objeto de negocio para consultar y buscar peliculas
 */
public class CarteleraBO {

    private final IServicioPeliculas servicio;

    public CarteleraBO(IServicioPeliculas servicio) {
        this.servicio = servicio;
    }

    /** Consulta las peliculas disponibles en cartelera */
    public CarteleraDTO consultar() {
        return servicio.consultarCartelera();
    }

    /** Obtiene el poster de una pelicula. */
    public byte[] obtenerPoster(PeliculaDTO pelicula) {
        return servicio.obtenerPoster(pelicula);
    }

    /** Busca peliculas por titulo para agregarlas al catalogo*/
    public List<PeliculaDTO> buscar(String texto) throws Exception {

       if (texto == null || texto.trim().length() < 2) {
    return List.of();
}

        return servicio.buscar(texto.trim());
    }
}
