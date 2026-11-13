package com.cineplus.negocio.bo;

import com.cineplus.dto.NuevaResenaDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.dto.ResenaDTO;

/**
 * Objeto de negocio de las resenas de la comunidad.
 *
 * @author luisf
 */
public class ResenaBO {
    private final PeliculaBO peliculaBO;

    public ResenaBO(PeliculaBO peliculaBO) {
        this.peliculaBO = peliculaBO;
    }

    /** Resenas mas recientes. */
    public PaginaDTO<ResenaDTO> listarRecientes(int pagina, int tamano) throws Exception {
        // Lo implementa el Desarrollador 5
        throw new Exception("Pendiente: ResenaBO.listarRecientes");
    }

    /** Resenas de una pelicula. */
    public PaginaDTO<ResenaDTO> listarPorPelicula(Integer idPelicula, int pagina, int tamano) throws Exception {
        // Lo implementa el Desarrollador 5
        throw new Exception("Pendiente: ResenaBO.listarPorPelicula");
    }

    /** Publica una resena (importa la pelicula si hace falta). */
    public ResenaDTO publicar(Integer idUsuario, NuevaResenaDTO datos) throws Exception {
        // Lo implementa el Desarrollador 5
        throw new Exception("Pendiente: ResenaBO.publicar");
    }
}
