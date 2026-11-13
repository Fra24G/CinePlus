package com.cineplus.negocio.bo;

import com.cineplus.dto.PeliculaDTO;
import com.cineplus.entidades.Pelicula;
import com.cineplus.servicio.IServicioPeliculas;
import java.util.List;
import java.util.Optional;

/**
 * Objeto de negocio del catalogo local de peliculas.
 *
 * @author luisf
 */
public class PeliculaBO {
    private final IServicioPeliculas servicio;

    public PeliculaBO(IServicioPeliculas servicio) {
        this.servicio = servicio;
    }

    /** Peliculas de la BD en cartelera hoy. */
    public List<PeliculaDTO> listarCartelera() throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaBO.listarCartelera");
    }

    /** Todo el catalogo (admin). */
    public List<PeliculaDTO> listarTodas() throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaBO.listarTodas");
    }

    /** Pelicula por id o Exception si no existe. */
    public PeliculaDTO obtener(Integer idPelicula) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaBO.obtener");
    }

    /** La pelicula de ese idExterno solo si hoy esta en cartelera. */
    public Optional<PeliculaDTO> buscarEnCartelera(String idExterno) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaBO.buscarEnCartelera");
    }

    /** La pelicula de ese idExterno si existe en el catalogo. */
    public Optional<PeliculaDTO> buscarPorIdExterno(String idExterno) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaBO.buscarPorIdExterno");
    }

    /** Crea (idPelicula null) o actualiza una pelicula. */
    public PeliculaDTO guardar(PeliculaDTO datos) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaBO.guardar");
    }

    /** Importa una pelicula de TMDB al catalogo sin duplicarla. */
    public PeliculaDTO importar(String idExterno, boolean enCartelera) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaBO.importar");
    }

    /** Activa o desactiva la pelicula en cartelera. */
    public void cambiarDisponibilidad(Integer idPelicula, boolean disponible) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: PeliculaBO.cambiarDisponibilidad");
    }

    /** Conversion entidad a DTO (la reutilizan ResenaBO y AdminBO). */
    static PeliculaDTO aDTO(Pelicula pelicula) {
        return new PeliculaDTO(
                pelicula.getIdPelicula(),
                pelicula.getIdExterno(),
                pelicula.getTitulo(),
                pelicula.getDirector(),
                pelicula.getGenero(),
                pelicula.getDuracionMinutos() != null ? pelicula.getDuracionMinutos() : 0,
                pelicula.getFechaEstreno(),
                pelicula.getClasificacion() != null ? pelicula.getClasificacion().getEtiqueta() : "",
                pelicula.getSinopsis(),
                pelicula.getImagen(),
                Boolean.TRUE.equals(pelicula.getDisponible()),
                pelicula.getInicioCartelera(),
                pelicula.getFinCartelera());
    }
}
