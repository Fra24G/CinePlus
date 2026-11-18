package com.cineplus.negocio.bo;

import com.cineplus.dto.NuevaResenaDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.ResenaDTO;
import com.cineplus.entidades.Pelicula;
import com.cineplus.entidades.Resena;
import com.cineplus.entidades.Usuario;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Objeto de negocio de las resenas de la comunidad.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public class ResenaBO {

    private final PeliculaBO peliculaBO;

    public ResenaBO(PeliculaBO peliculaBO) {
        this.peliculaBO = peliculaBO;
    }

    /** Resenas mas recientes paginadas. */
    public PaginaDTO<ResenaDTO> listarRecientes(int pagina, int tamano) throws Exception {
        int pag = Math.max(0, pagina);
        int tam = tamano <= 0 ? 10 : tamano;
        try (UnitOfWork uow = new UnitOfWork()) {
            List<ResenaDTO> elementos = uow.resenaDAO().listarRecientes(pag, tam)
                    .stream()
                    .map(this::aDTO)
                    .toList();
            long total = uow.resenaDAO().contarTodas();
            return new PaginaDTO<>(elementos, pag, tam, total);
        }
    }

    /** Resenas de una pelicula paginadas. */
    public PaginaDTO<ResenaDTO> listarPorPelicula(Integer idPelicula, int pagina, int tamano) throws Exception {
        int pag = Math.max(0, pagina);
        int tam = tamano <= 0 ? 10 : tamano;
        if (idPelicula == null) {
            return new PaginaDTO<>(List.of(), pag, tam, 0);
        }
        try (UnitOfWork uow = new UnitOfWork()) {
            List<ResenaDTO> elementos = uow.resenaDAO().listarPorPelicula(idPelicula, pag, tam)
                    .stream()
                    .map(this::aDTO)
                    .toList();
            long total = uow.resenaDAO().contarPorPelicula(idPelicula);
            return new PaginaDTO<>(elementos, pag, tam, total);
        }
    }

    /** Publica una resena e importa la pelicula al catalogo si viene solo de TMDB. */
    public ResenaDTO publicar(Integer idUsuario, NuevaResenaDTO datos) throws Exception {
        if (idUsuario == null) {
            throw new Exception("Inicia sesión para continuar.");
        }
        ValidacionesResena.resena(datos);

        Integer idPelicula = datos.idPelicula();
        if (idPelicula == null) {
            PeliculaDTO importada = peliculaBO.importar(datos.idExterno().trim(), false);
            idPelicula = importada.idPelicula();
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();

            Usuario usuario = uow.usuarioDAO().buscarPorId(idUsuario)
                    .orElseThrow(() -> new Exception("No se encontró el usuario."));
            Pelicula pelicula = uow.peliculaDAO().buscarPorId(idPelicula)
                    .orElseThrow(() -> new Exception("No se encontró la película."));

            if (uow.resenaDAO().existeDeUsuario(idUsuario, idPelicula)) {
                throw new Exception("Ya publicaste una reseña de esta película.");
            }

            Resena nueva = new Resena();
            nueva.setUsuario(usuario);
            nueva.setPelicula(pelicula);
            nueva.setCalificacion(datos.calificacion());
            nueva.setComentario(datos.comentario().trim());
            nueva.setFecha(LocalDateTime.now());

            uow.resenaDAO().insertar(nueva);
            uow.commit();
            return aDTO(nueva);
        }
    }

    private ResenaDTO aDTO(Resena resena) {
        return new ResenaDTO(
                resena.getIdResena(),
                resena.getPelicula().getIdPelicula(),
                resena.getPelicula().getTitulo(),
                resena.getUsuario().getNombreMostrado(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getFecha());
    }
}
