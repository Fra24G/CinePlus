
package com.cineplus.negocio.bo;

import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.PeliculaExternaDTO;
import com.cineplus.entidades.Pelicula;
import com.cineplus.entidades.enums.Clasificacion;
import com.cineplus.persistencia.uow.UnitOfWork;
import com.cineplus.servicio.IServicioPeliculas;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Objeto de negocio del catalogo local de peliculas
 */
public class PeliculaBO {

    private final IServicioPeliculas servicio;

    public PeliculaBO(IServicioPeliculas servicio) {
        this.servicio = servicio;
    }

    /** Peliculas de la BD en cartelera hoy */
    public List<PeliculaDTO> listarCartelera() throws Exception {
        try (UnitOfWork uow = new UnitOfWork()) {
            List<Pelicula> peliculas =
                    uow.peliculaDAO().listarEnCartelera(LocalDate.now());

            List<PeliculaDTO> resultado = new ArrayList<>();

            for (Pelicula pelicula : peliculas) {
                resultado.add(aDTO(pelicula));
            }

            return resultado;
        }
    }

    /** Todo el catalogo para administracion */
    public List<PeliculaDTO> listarTodas() throws Exception {
        try (UnitOfWork uow = new UnitOfWork()) {
            List<Pelicula> peliculas = uow.peliculaDAO().listarTodas();

            List<PeliculaDTO> resultado = new ArrayList<>();

            for (Pelicula pelicula : peliculas) {
                resultado.add(aDTO(pelicula));
            }

            return resultado;
        }
    }

    /** Busca una pelicula por su id */
    public PeliculaDTO obtener(Integer idPelicula) throws Exception {
        if (idPelicula == null || idPelicula <= 0) {
            throw new Exception("El id de la película no es válido.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            Optional<Pelicula> pelicula =
                    uow.peliculaDAO().buscarPorId(idPelicula);

            if (pelicula.isEmpty()) {
                throw new Exception("No se encontró la película.");
            }

            return aDTO(pelicula.get());
        }
    }

    /** Busca una pelicula externa solamente si esta en cartelera hoy */
    public Optional<PeliculaDTO> buscarEnCartelera(String idExterno)
            throws Exception {

        if (idExterno == null || idExterno.trim().isEmpty()) {
            return Optional.empty();
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            Optional<Pelicula> pelicula =
                    uow.peliculaDAO().buscarPorIdExterno(idExterno.trim());

            if (pelicula.isEmpty()) {
                return Optional.empty();
            }

            if (!estaEnCartelera(pelicula.get(), LocalDate.now())) {
                return Optional.empty();
            }

            return Optional.of(aDTO(pelicula.get()));
        }
    }

    /** Busca una pelicula por su id externo, aunque no este en cartelera */
    public Optional<PeliculaDTO> buscarPorIdExterno(String idExterno)
            throws Exception {

        if (idExterno == null || idExterno.trim().isEmpty()) {
            return Optional.empty();
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            Optional<Pelicula> pelicula =
                    uow.peliculaDAO().buscarPorIdExterno(idExterno.trim());

            if (pelicula.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(aDTO(pelicula.get()));
        }
    }

    /** Crea o actualiza una pelicula */
    public PeliculaDTO guardar(PeliculaDTO datos) throws Exception {
        ValidacionesCatalogo.pelicula(datos);

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();

            Pelicula pelicula;
            boolean nueva = datos.idPelicula() == null;

            if (nueva) {
                pelicula = new Pelicula();
            } else {
                Optional<Pelicula> encontrada =
                        uow.peliculaDAO().buscarPorId(datos.idPelicula());

                if (encontrada.isEmpty()) {
                    throw new Exception("No se encontró la película que deseas actualizar.");
                }

                pelicula = encontrada.get();
            }

            pelicula.setIdExterno(datos.idExterno());
            pelicula.setTitulo(datos.titulo().trim());
            pelicula.setDirector(datos.director());
            pelicula.setGenero(datos.genero());
            pelicula.setDuracionMinutos(datos.duracionMinutos());
            pelicula.setFechaEstreno(datos.fechaEstreno());
            pelicula.setClasificacion(
                    Clasificacion.porEtiqueta(datos.clasificacion()));
            pelicula.setSinopsis(datos.sinopsis());
            pelicula.setImagen(datos.imagen());
            pelicula.setDisponible(datos.disponible());
            pelicula.setInicioCartelera(datos.inicioCartelera());
            pelicula.setFinCartelera(datos.finCartelera());

            if (nueva) {
                uow.peliculaDAO().insertar(pelicula);
            } else {
                uow.peliculaDAO().actualizar(pelicula);
            }

            uow.commit();

            return aDTO(pelicula);
        }
    }

    /** Importa una pelicula de TMDB sin duplicarla*/
    public PeliculaDTO importar(String idExterno, boolean enCartelera)
            throws Exception {

        if (idExterno == null || idExterno.trim().isEmpty()) {
            throw new Exception("El id externo de la película es obligatorio.");
        }

        idExterno = idExterno.trim();

        Optional<PeliculaDTO> existente = buscarPorIdExterno(idExterno);

        // Si ya existe, no se vuelve a insertar
        if (existente.isPresent()) {
            PeliculaDTO pelicula = existente.get();

            if (!enCartelera) {
                return pelicula;
            }

            PeliculaDTO actualizada = new PeliculaDTO(
                    pelicula.idPelicula(),
                    pelicula.idExterno(),
                    pelicula.titulo(),
                    pelicula.director(),
                    pelicula.genero(),
                    pelicula.duracionMinutos(),
                    pelicula.fechaEstreno(),
                    pelicula.clasificacion(),
                    pelicula.sinopsis(),
                    pelicula.imagen(),
                    true,
                    LocalDate.now(),
                    null
            );

            return guardar(actualizada);
        }

        if (servicio == null) {
            throw new Exception("El servicio de películas no está disponible.");
        }

        PeliculaExternaDTO detalle = servicio.obtenerDetalle(idExterno);

        if (detalle == null) {
            throw new Exception("No se pudo obtener la información de TMDB.");
        }

        String idGuardado = detalle.idExterno();

        if (idGuardado == null || idGuardado.trim().isEmpty()) {
            idGuardado = idExterno;
        }

        LocalDate inicio = enCartelera ? LocalDate.now() : null;

        PeliculaDTO nueva = new PeliculaDTO(
                null,
                idGuardado,
                detalle.titulo(),
                detalle.director(),
                detalle.genero(),
                detalle.duracionMinutos(),
                detalle.fechaEstreno(),
                detalle.clasificacion(),
                detalle.sinopsis(),
                detalle.imagen(),
                enCartelera,
                inicio,
                null
        );

        return guardar(nueva);
    }

    /** Activa o desactiva una pelicula en cartelera */
    public void cambiarDisponibilidad(Integer idPelicula, boolean disponible)
            throws Exception {

        if (idPelicula == null || idPelicula <= 0) {
            throw new Exception("El id de la película no es válido.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();

            Optional<Pelicula> encontrada =
                    uow.peliculaDAO().buscarPorId(idPelicula);

            if (encontrada.isEmpty()) {
                throw new Exception("No se encontró la película.");
            }

            Pelicula pelicula = encontrada.get();
            pelicula.setDisponible(disponible);

            if (disponible) {
                pelicula.setInicioCartelera(LocalDate.now());
                pelicula.setFinCartelera(null);
            }

            uow.peliculaDAO().actualizar(pelicula);
            uow.commit();
        }
    }

    /** Comprueba si la pelicula esta disponible en la fecha indicada */
    private boolean estaEnCartelera(Pelicula pelicula, LocalDate fecha) {
        boolean inicioValido = pelicula.getInicioCartelera() == null
                || !fecha.isBefore(pelicula.getInicioCartelera());

        boolean finValido = pelicula.getFinCartelera() == null
                || !fecha.isAfter(pelicula.getFinCartelera());

        return Boolean.TRUE.equals(pelicula.getDisponible())
                && inicioValido
                && finValido;
    }

    /** Conversion de entidad a DTO */
    static PeliculaDTO aDTO(Pelicula pelicula) {
        return new PeliculaDTO(
                pelicula.getIdPelicula(),
                pelicula.getIdExterno(),
                pelicula.getTitulo(),
                pelicula.getDirector(),
                pelicula.getGenero(),
                pelicula.getDuracionMinutos() != null
                        ? pelicula.getDuracionMinutos() : 0,
                pelicula.getFechaEstreno(),
                pelicula.getClasificacion() != null
                        ? pelicula.getClasificacion().getEtiqueta() : "",
                pelicula.getSinopsis(),
                pelicula.getImagen(),
                Boolean.TRUE.equals(pelicula.getDisponible()),
                pelicula.getInicioCartelera(),
                pelicula.getFinCartelera()
        );
    }
}
