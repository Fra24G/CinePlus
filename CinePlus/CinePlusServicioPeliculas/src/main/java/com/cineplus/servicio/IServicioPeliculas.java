package com.cineplus.servicio;

import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.PeliculaExternaDTO;
import java.util.List;

/**
 * PATRON ADAPTER (objetivo). Contrato del servicio de peliculas. Negocio solo conoce
 * esta interfaz, no sabe si los datos vienen de TMDB o de otro proveedor.
 *
 * @author luisf
 */
public interface IServicioPeliculas {

    /** Peliculas en cartelera. Si TMDB no responde, devuelve el catalogo de respaldo. */
    CarteleraDTO consultarCartelera();

    /** Bytes de la imagen del poster, o un arreglo vacio si no se pudo obtener. */
    byte[] obtenerPoster(PeliculaDTO pelicula);

    /** Detalle de una pelicula de TMDB: director, duracion y clasificacion. */
    PeliculaExternaDTO obtenerDetalle(String idExterno) throws Exception;

    /** Busca peliculas por titulo en TMDB. */
    List<PeliculaDTO> buscar(String texto) throws Exception;

    /** Guarda un poster subido por el admin y devuelve la ruta "archivo:..." */
    String guardarPosterLocal(byte[] imagen, String nombreArchivo) throws Exception;
}
