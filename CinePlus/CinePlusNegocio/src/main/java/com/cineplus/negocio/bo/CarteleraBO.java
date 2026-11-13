package com.cineplus.negocio.bo;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.servicio.IServicioPeliculas;
import java.util.List;

/**
 * Objeto de negocio de las peliculas generales (TMDB). Solo conoce la interfaz del
 * servicio de peliculas, no sabe si los datos vienen de TMDB o de otro proveedor.
 *
 * @author luisf
 */
public class CarteleraBO {

    private final IServicioPeliculas servicio;

    public CarteleraBO(IServicioPeliculas servicio) {
        this.servicio = servicio;
    }

    public CarteleraDTO consultar() {
        return servicio.consultarCartelera();
    }

    public byte[] obtenerPoster(PeliculaDTO pelicula) {
        return servicio.obtenerPoster(pelicula);
    }

    /** Busca peliculas en TMDB por titulo (para "Anadir a cartelera"). */
    public List<PeliculaDTO> buscar(String texto) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: CarteleraBO.buscar");
    }
}
