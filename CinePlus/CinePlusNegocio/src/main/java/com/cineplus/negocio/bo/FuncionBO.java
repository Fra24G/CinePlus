package com.cineplus.negocio.bo;

import com.cineplus.dto.AsientoDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.GuardarFuncionDTO;
import com.cineplus.entidades.Funcion;
import java.util.List;

/**
 * Objeto de negocio de funciones y asientos.
 *
 * @author luisf
 */
public class FuncionBO {

    /** Funciones futuras de la pelicula en ese cine. */
    public List<FuncionDTO> listarPorPelicula(Integer idPelicula, Integer idCine) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionBO.listarPorPelicula");
    }

    /** Todas las funciones de la pelicula (admin). */
    public List<FuncionDTO> listarPorPeliculaAdmin(Integer idPelicula) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionBO.listarPorPeliculaAdmin");
    }

    /** Funcion por id o Exception si no existe. */
    public FuncionDTO obtener(Integer idFuncion) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionBO.obtener");
    }

    /** Crea o actualiza una funcion revisando empalmes. */
    public FuncionDTO guardar(GuardarFuncionDTO datos) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionBO.guardar");
    }

    /** Elimina una funcion sin boletos vendidos. */
    public void eliminar(Integer idFuncion) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionBO.eliminar");
    }

    /** Mapa de asientos con ocupados y precio. */
    public List<AsientoDTO> listarAsientos(Integer idFuncion) throws Exception {
        // Lo implementa el Desarrollador 2
        throw new Exception("Pendiente: FuncionBO.listarAsientos");
    }

    /** Conversion entidad a DTO. */
    static FuncionDTO aDTO(Funcion funcion) {
        return new FuncionDTO(
                funcion.getIdFuncion(),
                funcion.getPelicula().getIdPelicula(),
                funcion.getPelicula().getTitulo(),
                funcion.getSala().getIdSala(),
                funcion.getSala().getNumeroSala(),
                ConvertidorCompra.nombreCine(funcion.getSala().getCine()),
                funcion.getTipo() != null ? funcion.getTipo().getEtiqueta() : "",
                funcion.getFormato() != null ? funcion.getFormato().getEtiqueta() : "",
                funcion.getSala().getCapacidadTotal() != null ? funcion.getSala().getCapacidadTotal() : 0,
                funcion.getFechaHora());
    }
}
