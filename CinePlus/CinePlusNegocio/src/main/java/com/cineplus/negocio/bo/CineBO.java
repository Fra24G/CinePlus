
package com.cineplus.negocio.bo;

import com.cineplus.dto.CineDTO;
import com.cineplus.dto.SalaDTO;
import com.cineplus.entidades.Cine;
import com.cineplus.entidades.Sala;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.util.List;

/**
 * Objeto de negocio encargado de consultar cines y salas.
 */
public class CineBO {

    /**
     * Obtiene todos los cines registrados.
     */
    public List<CineDTO> listarCines() throws Exception {

        try (UnitOfWork uow = new UnitOfWork()) {

            // Consultamos los cines mediante el DAO.
            List<Cine> cines = uow.cineDAO().listarTodos();

            // Convertimos las entidades en DTO para presentacion.
            return cines.stream()
                    .map(CineBO::aDTO)
                    .toList();
        }
    }

    /**
     * Obtiene las salas que pertenecen a un cine.
     */
    public List<SalaDTO> listarSalas(Integer idCine) throws Exception {

        if (idCine == null || idCine <= 0) {
            throw new Exception("Selecciona un cine válido.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {

            // Verificamos que el cine exista
            if (uow.cineDAO().buscarPorId(idCine).isEmpty()) {
                throw new Exception("El cine seleccionado no existe.");
            }

            // Consultamos las salas de ese cine
            List<Sala> salas = uow.salaDAO().listarPorCine(idCine);

            // Convertimos cada sala en un DTO
            return salas.stream()
                    .map(CineBO::aDTO)
                    .toList();
        }
    }

    /**
     * Convierte un cine en CineDTO
     */
    private static CineDTO aDTO(Cine cine) {
        return new CineDTO(
                cine.getIdCine(),
                cine.getNombre(),
                cine.getCiudad()
        );
    }

    /**
     * Convierte una sala en SalaDTO
     */
    private static SalaDTO aDTO(Sala sala) {
        return new SalaDTO(
                sala.getIdSala(),
                sala.getCine().getIdCine(),
                sala.getNumeroSala(),
                sala.getCapacidadTotal(),
                sala.getTipoSala()
        );
    }
}
