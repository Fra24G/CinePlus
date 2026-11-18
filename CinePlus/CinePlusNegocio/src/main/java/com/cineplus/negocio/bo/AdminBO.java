package com.cineplus.negocio.bo;

import com.cineplus.dto.PeliculaAdminDTO;
import com.cineplus.dto.ResumenAdminDTO;
import com.cineplus.entidades.Pelicula;
import com.cineplus.persistencia.uow.UnitOfWork;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de negocio del panel de control del administrador.
 *
 * @author Alec Enrique Navarro Gonzalez
 */
public class AdminBO {

    /** Boletos vendidos, funciones publicadas y peliculas de la semana actual (lunes a domingo). */
    public ResumenAdminDTO resumenSemana() throws Exception {
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime inicioSemana = lunes.atStartOfDay();
        LocalDateTime finSemana = domingo.atTime(LocalTime.MAX);
        LocalDateTime ahora = LocalDateTime.now();

        try (UnitOfWork uow = new UnitOfWork()) {
            long boletosSemana = uow.compraDAO().contarBoletosVendidosEntre(inicioSemana, finSemana);
            long funcionesSemana = uow.funcionDAO().contarEntre(inicioSemana, finSemana);

            List<Pelicula> catalogo = uow.peliculaDAO().listarTodas();
            List<PeliculaAdminDTO> peliculasAdmin = new ArrayList<>(catalogo.size());

            for (Pelicula pelicula : catalogo) {
                long futuras = uow.funcionDAO().contarFuturasPorPelicula(pelicula.getIdPelicula(), ahora);
                peliculasAdmin.add(new PeliculaAdminDTO(PeliculaBO.aDTO(pelicula), futuras));
            }

            return new ResumenAdminDTO(boletosSemana, funcionesSemana, peliculasAdmin);
        }
    }
}
