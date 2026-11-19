
package com.cineplus.negocio.bo;

import com.cineplus.dto.AsientoDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.GuardarFuncionDTO;
import com.cineplus.entidades.Asiento;
import com.cineplus.entidades.Funcion;
import com.cineplus.entidades.Pelicula;
import com.cineplus.entidades.Sala;
import com.cineplus.entidades.enums.FormatoFuncion;
import com.cineplus.entidades.enums.TipoFuncion;
import com.cineplus.negocio.compra.ReglasPrecio;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/*
 * Objeto de negocio de funciones y asientos
 */
public class FuncionBO {

    /** Funciones futuras de una pelicula en un cine */
    public List<FuncionDTO> listarPorPelicula(
            Integer idPelicula, Integer idCine) throws Exception {

        if (idPelicula == null || idPelicula <= 0
                || idCine == null || idCine <= 0) {
            throw new Exception("La película y el cine son obligatorios.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            List<Funcion> funciones = uow.funcionDAO().listarPorPelicula(
                    idPelicula, idCine, LocalDateTime.now());

            List<FuncionDTO> resultado = new ArrayList<>();

            for (Funcion funcion : funciones) {
                resultado.add(aDTO(funcion));
            }

            return resultado;
        }
    }

    /** Todas las funciones de una pelicula para administracion */
    public List<FuncionDTO> listarPorPeliculaAdmin(
            Integer idPelicula) throws Exception {

        if (idPelicula == null || idPelicula <= 0) {
            throw new Exception("El id de la película no es válido.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            List<Funcion> funciones =
                    uow.funcionDAO().listarPorPeliculaAdmin(idPelicula);

            List<FuncionDTO> resultado = new ArrayList<>();

            for (Funcion funcion : funciones) {
                resultado.add(aDTO(funcion));
            }

            return resultado;
        }
    }

    /** Busca una funcion por su id */
    public FuncionDTO obtener(Integer idFuncion) throws Exception {
        if (idFuncion == null || idFuncion <= 0) {
            throw new Exception("El id de la función no es válido.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            Optional<Funcion> encontrada =
                    uow.funcionDAO().buscarPorId(idFuncion);

            if (encontrada.isEmpty()) {
                throw new Exception("No se encontró la función.");
            }

            return aDTO(encontrada.get());
        }
    }

    /** Crea o actualiza una funcion revisando los empalmes */
    public FuncionDTO guardar(GuardarFuncionDTO datos) throws Exception {
        ValidacionesCatalogo.funcion(datos);

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();

            // Comprueba que la pelicula exista
            Optional<Pelicula> peliculaEncontrada =
                    uow.peliculaDAO().buscarPorId(datos.idPelicula());

            if (peliculaEncontrada.isEmpty()) {
                throw new Exception("No se encontró la película.");
            }

            // Comprueba que la sala exista
            Optional<Sala> salaEncontrada =
                    uow.salaDAO().buscarPorId(datos.idSala());

            if (salaEncontrada.isEmpty()) {
                throw new Exception("No se encontró la sala.");
            }

            Pelicula pelicula = peliculaEncontrada.get();
            Sala sala = salaEncontrada.get();

            Funcion funcion;
            boolean nueva = datos.idFuncion() == null;

            if (nueva) {
                funcion = new Funcion();
            } else {
                Optional<Funcion> funcionEncontrada =
                        uow.funcionDAO().buscarPorId(datos.idFuncion());

                if (funcionEncontrada.isEmpty()) {
                    throw new Exception("No se encontró la función.");
                }

                funcion = funcionEncontrada.get();

                // Si ya se vendieron boletos, no se permite cambiar
                // la sala ni la fecha de la funcion
                long boletosVendidos =
                        uow.boletoDAO().contarPorFuncion(datos.idFuncion());

                if (boletosVendidos > 0) {
                    boolean cambioSala = !Objects.equals(
                            funcion.getSala().getIdSala(),
                            datos.idSala());

                    boolean cambioFecha = !Objects.equals(
                            funcion.getFechaHora(),
                            datos.fechaHora());

                    if (cambioSala || cambioFecha) {
                        throw new Exception(
                                "No se puede cambiar la sala ni la fecha: "
                                + "la función ya tiene boletos vendidos.");
                    }
                }
            }

            // Revisa que no exista otra función en ese horario
            revisarEmpalme(uow, datos, pelicula);

            funcion.setPelicula(pelicula);
            funcion.setSala(sala);
            funcion.setFechaHora(datos.fechaHora());
            funcion.setTipo(TipoFuncion.valueOf(datos.tipo()));
            funcion.setFormato(FormatoFuncion.valueOf(datos.formato()));

            Funcion guardada;

            if (nueva) {
                guardada = uow.funcionDAO().insertar(funcion);
            } else {
                guardada = uow.funcionDAO().actualizar(funcion);
            }

            uow.commit();

            return aDTO(guardada);
        }
    }

    /** Revisa si el horario se empalma con otra funcion de la sala*/
    private void revisarEmpalme(
            UnitOfWork uow,
            GuardarFuncionDTO datos,
            Pelicula pelicula) throws Exception {

        LocalDateTime inicioNuevo = datos.fechaHora();

        int duracionNueva = pelicula.getDuracionMinutos() != null
                ? pelicula.getDuracionMinutos() : 0;

        // Se agregan 20 minutos para limpieza de la sala
        LocalDateTime finNuevo =
                inicioNuevo.plusMinutes(duracionNueva + 20L);

        // La duracion maxima validada es de 600 minutos
        // Se consultan tambien funciones que comenzaron antes del horario nuevo
        LocalDateTime desde = inicioNuevo.minusMinutes(620);

        List<Funcion> funciones = uow.funcionDAO().listarPorSalaEntre(
                datos.idSala(), desde, finNuevo);

        for (Funcion otra : funciones) {

            // Al editar, no se compara la funcion consigo misma
            if (datos.idFuncion() != null
                    && Objects.equals(
                            otra.getIdFuncion(), datos.idFuncion())) {
                continue;
            }

            LocalDateTime inicioOtra = otra.getFechaHora();

            int duracionOtra =
                    otra.getPelicula().getDuracionMinutos() != null
                    ? otra.getPelicula().getDuracionMinutos() : 0;

            LocalDateTime finOtra =
                    inicioOtra.plusMinutes(duracionOtra + 20L);

            boolean seEmpalman =
                    !inicioNuevo.isAfter(finOtra)
                    && !inicioOtra.isAfter(finNuevo);

            if (seEmpalman) {
                throw new Exception(
                        "La sala ya tiene una función en ese horario.");
            }
        }
    }

    /** Elimina una funcion si no tiene boletos vendidos */
    public void eliminar(Integer idFuncion) throws Exception {
        if (idFuncion == null || idFuncion <= 0) {
            throw new Exception("El id de la función no es válido.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            uow.iniciar();

            Optional<Funcion> encontrada =
                    uow.funcionDAO().buscarPorId(idFuncion);

            if (encontrada.isEmpty()) {
                throw new Exception("No se encontró la función.");
            }

            long boletosVendidos =
                    uow.boletoDAO().contarPorFuncion(idFuncion);

            if (boletosVendidos > 0) {
                throw new Exception(
                        "No se puede eliminar: ya tiene boletos vendidos.");
            }

            uow.funcionDAO().eliminar(encontrada.get());
            uow.commit();
        }
    }

    /** Obtiene los asientos de una funcion con su precio y disponibilidad */
    public List<AsientoDTO> listarAsientos(Integer idFuncion)
            throws Exception {

        if (idFuncion == null || idFuncion <= 0) {
            throw new Exception("El id de la función no es válido.");
        }

        try (UnitOfWork uow = new UnitOfWork()) {
            Optional<Funcion> encontrada =
                    uow.funcionDAO().buscarPorId(idFuncion);

            if (encontrada.isEmpty()) {
                throw new Exception("No se encontró la función.");
            }

            Funcion funcion = encontrada.get();

            // Obtiene los asientos activos de la sala
            List<Asiento> asientos =
                    uow.asientoDAO().listarPorSala(
                            funcion.getSala().getIdSala());

            // Obtiene los ids de los asientos que ya fueron vendidos
            List<Integer> idsOcupados =
                    uow.boletoDAO().listarAsientosOcupados(idFuncion);

            Set<Integer> ocupados = new HashSet<>(idsOcupados);

            List<AsientoDTO> resultado = new ArrayList<>();

            for (Asiento asiento : asientos) {
                String tipo = asiento.getTipo() != null
                        ? asiento.getTipo().getEtiqueta() : "";

                double precio =
                        ReglasPrecio.precioAsiento(asiento.getTipo());

                boolean estaOcupado =
                        ocupados.contains(asiento.getIdAsiento());

                resultado.add(new AsientoDTO(
                        asiento.getIdAsiento(),
                        asiento.getFila(),
                        asiento.getNumero() != null
                                ? asiento.getNumero() : 0,
                        tipo,
                        precio,
                        estaOcupado
                ));
            }

            return resultado;
        }
    }

    /** Conversion de entidad a DTO */
    static FuncionDTO aDTO(Funcion funcion) {
        return new FuncionDTO(
                funcion.getIdFuncion(),
                funcion.getPelicula().getIdPelicula(),
                funcion.getPelicula().getTitulo(),
                funcion.getSala().getIdSala(),
                funcion.getSala().getNumeroSala(),
                ConvertidorCompra.nombreCine(funcion.getSala().getCine()),
                funcion.getTipo() != null
                        ? funcion.getTipo().getEtiqueta() : "",
                funcion.getFormato() != null
                        ? funcion.getFormato().getEtiqueta() : "",
                funcion.getSala().getCapacidadTotal() != null
                        ? funcion.getSala().getCapacidadTotal() : 0,
                funcion.getFechaHora()
        );
    }
}
