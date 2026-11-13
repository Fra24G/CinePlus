package com.cineplus.negocio.carga;

import com.cineplus.entidades.Asiento;
import com.cineplus.entidades.Boleto;
import com.cineplus.entidades.Cine;
import com.cineplus.entidades.Compra;
import com.cineplus.entidades.Funcion;
import com.cineplus.entidades.Membresia;
import com.cineplus.entidades.Pelicula;
import com.cineplus.entidades.Resena;
import com.cineplus.entidades.Sala;
import com.cineplus.entidades.Usuario;
import com.cineplus.entidades.enums.Clasificacion;
import com.cineplus.entidades.enums.EstadoPago;
import com.cineplus.entidades.enums.FormatoFuncion;
import com.cineplus.entidades.enums.MetodoPago;
import com.cineplus.entidades.enums.Rol;
import com.cineplus.entidades.enums.TipoAsiento;
import com.cineplus.entidades.enums.TipoFuncion;
import com.cineplus.negocio.compra.ReglasPrecio;
import com.cineplus.negocio.seguridad.HashContrasena;
import com.cineplus.persistencia.uow.UnitOfWork;
import com.cineplus.servicio.ServicioPeliculasTmdb;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Datos semilla para que todo el equipo pruebe sin esperar a otros.
 * Solo actua si la BD no tiene cines. 
 * Las fechas son relativas a hoy, asi la semilla sirve cualquier dia.
 *
 * Usuarios de prueba (contrasena CinePlus2026):
 *   admin@cineplus.com (ADMIN), cliente@cineplus.com (120 puntos), pro@cineplus.com (CinePLUS Pro).
 * Quitar o cambiar estos usuarios antes de la entrega final.
 *
 * @author luisf
 */
public class CargaInicial {

    /** Contrasena de prueba de los tres usuarios semilla. */
    public static final String CONTRASENA_PRUEBA = "CinePlus2026";

    private static final LocalTime[] HORARIOS = {
        LocalTime.of(12, 0), LocalTime.of(15, 30), LocalTime.of(19, 0), LocalTime.of(22, 30)
    };
    private static final int DIAS_DE_FUNCIONES = 7;

    private final HashContrasena hash = new HashContrasena();

    // Pelicula que solo esta en el catalogo (para la resena de prueba)
    private Pelicula soloCatalogo;

    /** Carga los datos semilla si la base de datos esta vacia. */
    public void ejecutar() throws Exception {
        if ("false".equalsIgnoreCase(System.getProperty("cineplus.semilla"))) {
            return;
        }
        try (UnitOfWork uow = new UnitOfWork()) {
            if (uow.cineDAO().contar() > 0) {
                return;
            }
            uow.iniciar();
            LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

            Map<String, Usuario> usuarios = crearUsuarios(uow);
            List<Sala> salas = crearCinesYSalas(uow);
            List<Pelicula> cartelera = crearPeliculas(uow);
            List<Funcion> regulares = crearFunciones(uow, cartelera, salas, ahora);

            // Sala 3 del Cine 1: funciones especiales para probar cancelacion e historial
            Sala especial = salas.get(2);
            Funcion proxima = crearFuncion(uow, cartelera.get(0), especial,
                    ahora.plusMinutes(90), TipoFuncion.ESPANOL);
            Funcion pasada = crearFuncion(uow, cartelera.get(1), especial,
                    ahora.minusDays(1).withHour(20).withMinute(0), TipoFuncion.SUBTITULADA);

            crearCompras(uow, usuarios, regulares, proxima, pasada, ahora);
            crearMembresiaPro(uow, usuarios.get("pro"), ahora);
            crearResenas(uow, usuarios, cartelera, ahora);
            uow.commit();
        }
    }

    // ---------- usuarios ----------

    /** admin (ADMIN), cliente (120 puntos) y pro (CLIENTE con CinePLUS Pro). */
    private Map<String, Usuario> crearUsuarios(UnitOfWork uow) throws Exception {
        Map<String, Usuario> usuarios = new LinkedHashMap<>();
        usuarios.put("admin", crearUsuario(uow, "Administrador CinePlus", "admin",
                "admin@cineplus.com", Rol.ADMIN, 0, LocalDate.of(1990, 1, 15)));
        usuarios.put("cliente", crearUsuario(uow, "Yazid Enrique Revilla Bernal", "yazid",
                "cliente@cineplus.com", Rol.CLIENTE, 120, LocalDate.of(2002, 6, 10)));
        usuarios.put("pro", crearUsuario(uow, "Maria Celeste Ochoa", "maria",
                "pro@cineplus.com", Rol.CLIENTE, 300, LocalDate.of(1998, 3, 22)));
        return usuarios;
    }

    /** Si el correo ya existe (por ejemplo, registrado a mano) lo reutiliza. */
    private Usuario crearUsuario(UnitOfWork uow, String nombre, String nombreUsuario, String correo,
            Rol rol, int puntos, LocalDate nacimiento) throws Exception {
        var existente = uow.usuarioDAO().buscarPorCorreo(correo);
        if (existente.isPresent()) {
            return existente.get();
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setCorreo(correo);
        usuario.setTelefono("6441234567");
        usuario.setFechaNacimiento(nacimiento);
        usuario.setRol(rol);
        usuario.setPuntosAcumulados(puntos);
        usuario.setContrasena(hash.crear(CONTRASENA_PRUEBA.toCharArray()));
        return uow.usuarioDAO().insertar(usuario);
    }

    // ---------- cines, salas y asientos ----------

    /** Cine 1 con salas 1, 2 y 3 (la 3 es para funciones especiales) y Cine 2 con salas 1 y 2. */
    private List<Sala> crearCinesYSalas(UnitOfWork uow) throws Exception {
        Cine cine1 = uow.cineDAO().insertar(new Cine("Cine 1", "Cd. Obregón"));
        Cine cine2 = uow.cineDAO().insertar(new Cine("Cine 2", "Cd. Obregón"));
        List<Sala> salas = new ArrayList<>();
        salas.add(uow.salaDAO().insertar(crearSala(cine1, 1, 8, 12)));
        salas.add(uow.salaDAO().insertar(crearSala(cine1, 2, 8, 12)));
        salas.add(uow.salaDAO().insertar(crearSala(cine1, 3, 8, 12)));
        salas.add(uow.salaDAO().insertar(crearSala(cine2, 1, 8, 12)));
        salas.add(uow.salaDAO().insertar(crearSala(cine2, 2, 8, 12)));
        return salas;
    }

    /** Sala con asientos Clasico: filas A, B, C... y numeros 1, 2, 3... */
    private Sala crearSala(Cine cine, int numero, int filas, int asientosPorFila) {
        Sala sala = new Sala();
        sala.setCine(cine);
        sala.setNumeroSala(numero);
        sala.setCapacidadTotal(filas * asientosPorFila);
        sala.setTipoSala("Tradicional");
        for (int f = 0; f < filas; f++) {
            String fila = String.valueOf((char) ('A' + f));
            for (int n = 1; n <= asientosPorFila; n++) {
                Asiento asiento = new Asiento(fila, n);
                asiento.setTipo(TipoAsiento.CLASICO);
                asiento.setEstado(true);
                sala.agregarAsiento(asiento);
            }
        }
        return sala;
    }

    // ---------- peliculas ----------

    /** Las 6 de muestra.json en cartelera, 1 solo de catalogo y 1 con el periodo vencido. Devuelve las 6. */
    private List<Pelicula> crearPeliculas(UnitOfWork uow) throws Exception {
        LocalDate hoy = LocalDate.now();
        List<Pelicula> cartelera = new ArrayList<>();
        cartelera.add(crearPelicula(uow, "157336", "Interestelar", "Christopher Nolan", "Ciencia ficción",
                169, 2014, Clasificacion.PG_13, "Un grupo de exploradores viaja a través de un agujero de gusano "
                + "en busca de un nuevo hogar para la humanidad.", "interestelar.jpg", true, hoy.minusDays(7), hoy.plusDays(21)));
        cartelera.add(crearPelicula(uow, "414906", "Batman", "Matt Reeves", "Suspenso",
                176, 2022, Clasificacion.PG_13, "Batman sigue la pista de un asesino que deja acertijos "
                + "mientras destapa la corrupción de Ciudad Gótica.", "batman.jpg", true, hoy.minusDays(7), hoy.plusDays(21)));
        cartelera.add(crearPelicula(uow, "27205", "El origen", "Christopher Nolan", "Ciencia ficción",
                148, 2010, Clasificacion.PG_13, "Un ladrón que roba secretos a través de los sueños recibe "
                + "la misión de implantar una idea.", "el-origen.jpg", true, hoy.minusDays(7), hoy.plusDays(21)));
        cartelera.add(crearPelicula(uow, "862", "Toy Story", "John Lasseter", "Animación",
                81, 1995, Clasificacion.G, "Los juguetes de Andy cobran vida cuando nadie los ve.",
                "toy-story.jpg", true, hoy.minusDays(7), hoy.plusDays(21)));
        cartelera.add(crearPelicula(uow, "354912", "Coco", "Lee Unkrich", "Animación",
                105, 2017, Clasificacion.PG, "Miguel viaja a la Tierra de los Muertos para descubrir "
                + "la historia de su familia.", "coco.jpg", true, hoy.minusDays(7), hoy.plusDays(21)));
        cartelera.add(crearPelicula(uow, "438631", "Duna", "Denis Villeneuve", "Aventura",
                155, 2021, Clasificacion.PG_13, "Paul Atreides llega a Arrakis, el planeta más peligroso "
                + "del universo.", "duna.jpg", true, hoy.minusDays(7), hoy.plusDays(21)));
        // Solo catalogo: se puede reseñar pero no esta en cartelera
        soloCatalogo = crearPelicula(uow, "872585", "Oppenheimer", "Christopher Nolan", "Historia",
                180, 2023, Clasificacion.R, "La historia del físico que dirigió el desarrollo de la bomba atómica.",
                null, false, null, null);
        // Periodo de cartelera vencido: no debe aparecer en Cartelera
        crearPelicula(uow, "603", "Matrix", "Lana y Lilly Wachowski", "Ciencia ficción",
                136, 1999, Clasificacion.R, "Un programador descubre que la realidad es una simulación.",
                null, true, hoy.minusDays(60), hoy.minusDays(30));
        return cartelera;
    }

    private Pelicula crearPelicula(UnitOfWork uow, String idExterno, String titulo, String director,
            String genero, int duracion, int anio, Clasificacion clasificacion, String sinopsis,
            String posterLocal, boolean disponible, LocalDate inicio, LocalDate fin) throws Exception {
        Pelicula pelicula = new Pelicula();
        pelicula.setIdExterno(idExterno);
        pelicula.setTitulo(titulo);
        pelicula.setDirector(director);
        pelicula.setGenero(genero);
        pelicula.setDuracionMinutos(duracion);
        pelicula.setFechaEstreno(LocalDate.of(anio, 1, 1));
        pelicula.setClasificacion(clasificacion);
        pelicula.setSinopsis(sinopsis);
        pelicula.setImagen(posterLocal == null ? null : ServicioPeliculasTmdb.PREFIJO_RECURSO + posterLocal);
        pelicula.setDisponible(disponible);
        pelicula.setInicioCartelera(inicio);
        pelicula.setFinCartelera(fin);
        return uow.peliculaDAO().insertar(pelicula);
    }

    // ---------- funciones ----------

    /**
     * Los proximos 7 dias (empezando hoy), 4 horarios por sala en las salas regulares
     * (todas menos la 3 del Cine 1). Las peliculas rotan; Español y Subtitulada se alternan.
     * Los horarios estan separados 3.5 horas, asi ninguna funcion se empalma.
     */
    private List<Funcion> crearFunciones(UnitOfWork uow, List<Pelicula> peliculas, List<Sala> salas,
            LocalDateTime ahora) throws Exception {
        List<Sala> regulares = List.of(salas.get(0), salas.get(1), salas.get(3), salas.get(4));
        List<Funcion> funciones = new ArrayList<>();
        int turno = 0;
        for (int dia = 0; dia < DIAS_DE_FUNCIONES; dia++) {
            LocalDate fecha = ahora.toLocalDate().plusDays(dia);
            for (Sala sala : regulares) {
                for (int h = 0; h < HORARIOS.length; h++) {
                    Pelicula pelicula = peliculas.get(turno % peliculas.size());
                    TipoFuncion tipo = (turno % 2 == 0) ? TipoFuncion.ESPANOL : TipoFuncion.SUBTITULADA;
                    funciones.add(crearFuncion(uow, pelicula, sala, fecha.atTime(HORARIOS[h]), tipo));
                    turno++;
                }
            }
        }
        return funciones;
    }

    private Funcion crearFuncion(UnitOfWork uow, Pelicula pelicula, Sala sala, LocalDateTime fechaHora,
            TipoFuncion tipo) throws Exception {
        Funcion funcion = new Funcion();
        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setFechaHora(fechaHora);
        funcion.setTipo(tipo);
        funcion.setFormato(FormatoFuncion.DOS_D);
        return uow.funcionDAO().insertar(funcion);
    }

    // ---------- compras ----------

    /**
     * cliente: aprobada futura (cancelable), aprobada dentro de 90 min (no cancelable), aprobada pasada,
     * cancelada con motivo y denegada sin boletos. pro: aprobada sin cargo de servicio y con puntos triples.
     */
    private void crearCompras(UnitOfWork uow, Map<String, Usuario> usuarios, List<Funcion> regulares,
            Funcion proxima, Funcion pasada, LocalDateTime ahora) throws Exception {
        Usuario cliente = usuarios.get("cliente");
        Usuario pro = usuarios.get("pro");
        Funcion futura = primeraFuncionDesde(regulares, ahora.plusDays(2));
        Funcion otraFutura = primeraFuncionDesde(regulares, ahora.plusDays(3));
        Funcion manana = primeraFuncionDesde(regulares, ahora.plusDays(1));
        Funcion paraPro = primeraFuncionDesde(regulares, ahora.plusDays(4));

        guardar(uow, crearCompra(cliente, futura, asientos(futura, "H2", "H3"), EstadoPago.APROBADO,
                false, MetodoPago.TARJETA, "2928", ahora.minusDays(1)));
        guardar(uow, crearCompra(cliente, proxima, asientos(proxima, "A1"), EstadoPago.APROBADO,
                false, MetodoPago.APPLE_GOOGLE_PAY, null, ahora.minusDays(2)));
        guardar(uow, crearCompra(cliente, pasada, asientos(pasada, "C4", "C5"), EstadoPago.APROBADO,
                false, MetodoPago.TARJETA, "2928", ahora.minusDays(4)));

        Compra cancelada = crearCompra(cliente, otraFutura, asientos(otraFutura, "D7"), EstadoPago.CANCELADO,
                false, MetodoPago.TARJETA, "2928", ahora.minusDays(6));
        cancelada.setMotivoCancelacion("Ya no podré asistir a la función.");
        cancelada.setFechaCancelacion(ahora.minusDays(5));
        guardar(uow, cancelada);

        // Pago denegado: se guarda sin boletos (no ocupa asientos)
        Compra denegada = crearCompra(cliente, manana, asientos(manana, "B1", "B2"), EstadoPago.DENEGADO,
                false, MetodoPago.TARJETA, "0000", ahora.minusDays(3));
        denegada.getBoletos().clear();
        denegada.setPuntosGanados(0);
        guardar(uow, denegada);

        guardar(uow, crearCompra(pro, paraPro, asientos(paraPro, "E5", "E6"), EstadoPago.APROBADO,
                true, MetodoPago.TARJETA, "4410", ahora.minusDays(1)));
    }

    /** Arma una compra con sus boletos y montos calculados con ReglasPrecio. */
    private Compra crearCompra(Usuario usuario, Funcion funcion, List<Asiento> asientos, EstadoPago estado,
            boolean pro, MetodoPago metodo, String ultimosDigitos, LocalDateTime fechaCompra) {
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        compra.setFuncion(funcion);
        compra.setFechaCompra(fechaCompra);
        double subtotal = 0;
        for (Asiento asiento : asientos) {
            double precio = ReglasPrecio.precioAsiento(asiento.getTipo());
            subtotal += precio;
            Boleto boleto = new Boleto();
            boleto.setFuncion(funcion);
            boleto.setAsiento(asiento);
            boleto.setPrecio(precio);
            boleto.setCodigo("CP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            compra.agregarBoleto(boleto);
        }
        double cargo = ReglasPrecio.cargoServicio(pro);
        double total = ReglasPrecio.redondear(subtotal + cargo);
        compra.setSubtotalBoletos(ReglasPrecio.redondear(subtotal));
        compra.setCargoServicio(cargo);
        compra.setMontoTotal(total);
        compra.setPuntosUsados(0);
        compra.setDescuentoPuntos(0.0);
        compra.setMontoCobrado(total);
        compra.setPuntosGanados(ReglasPrecio.puntosGanados(total, pro));
        compra.setMetodoPago(metodo);
        compra.setUltimosDigitos(ultimosDigitos);
        compra.setEstadoPago(estado);
        return compra;
    }

    private void guardar(UnitOfWork uow, Compra compra) throws Exception {
        uow.compraDAO().insertar(compra);
    }

    /** Primera funcion (de las regulares) que empieza en o despues de esa fecha y hora. */
    private Funcion primeraFuncionDesde(List<Funcion> funciones, LocalDateTime desde) throws Exception {
        return funciones.stream()
                .filter(f -> !f.getFechaHora().isBefore(desde))
                .findFirst()
                .orElseThrow(() -> new Exception("Semilla: no hay funciones después de " + desde));
    }

    /** Busca asientos de la sala de la funcion por etiqueta, por ejemplo "H2". */
    private List<Asiento> asientos(Funcion funcion, String... etiquetas) throws Exception {
        List<Asiento> encontrados = new ArrayList<>();
        for (String etiqueta : etiquetas) {
            String fila = etiqueta.substring(0, 1);
            int numero = Integer.parseInt(etiqueta.substring(1));
            Asiento asiento = funcion.getSala().getAsientos().stream()
                    .filter(a -> a.getFila().equals(fila) && a.getNumero() == numero)
                    .findFirst()
                    .orElseThrow(() -> new Exception("Semilla: no existe el asiento " + etiqueta));
            encontrados.add(asiento);
        }
        return encontrados;
    }

    // ---------- membresia y resenas ----------

    /** CinePLUS Pro activa: empezo hace 10 dias y vence en 20. */
    private void crearMembresiaPro(UnitOfWork uow, Usuario usuario, LocalDateTime ahora) throws Exception {
        Membresia membresia = new Membresia();
        membresia.setUsuario(usuario);
        membresia.setFechaInicio(ahora.minusDays(10));
        membresia.setFechaVencimiento(ahora.plusDays(20));
        membresia.setEstado(true);
        membresia.setNombrePlan("CinePLUS Pro");
        membresia.setPrecioMensual(129.0);
        membresia.setBeneficios("Más puntos por tus compras, sin comisión por uso del servicio, "
                + "preventas anticipadas y funciones exclusivas.");
        uow.membresiaDAO().insertar(membresia);
    }

    /** 5 resenas en 3 peliculas, incluida la que solo esta en el catalogo. */
    private void crearResenas(UnitOfWork uow, Map<String, Usuario> usuarios, List<Pelicula> cartelera,
            LocalDateTime ahora) throws Exception {
        crearResena(uow, usuarios.get("cliente"), cartelera.get(0), 5,
                "Visualmente impresionante y muy emotiva. La música de Hans Zimmer es increíble.", ahora.minusDays(2));
        crearResena(uow, usuarios.get("pro"), cartelera.get(0), 4,
                "Una gran película de ciencia ficción, aunque la parte final es algo confusa.", ahora.minusDays(5));
        crearResena(uow, usuarios.get("cliente"), cartelera.get(1), 4,
                "Un Batman más detectivesco y oscuro. Muy buena atmósfera.", ahora.minusDays(1));
        crearResena(uow, usuarios.get("pro"), cartelera.get(1), 3,
                "Me gustó, pero se siente demasiado larga.", ahora.minusDays(8));
        crearResena(uow, usuarios.get("pro"), soloCatalogo, 5,
                "Actuaciones impresionantes y una historia que te deja pensando.", ahora.minusDays(3));
    }

    private void crearResena(UnitOfWork uow, Usuario usuario, Pelicula pelicula, int calificacion,
            String comentario, LocalDateTime fecha) throws Exception {
        Resena resena = new Resena();
        resena.setUsuario(usuario);
        resena.setPelicula(pelicula);
        resena.setCalificacion(calificacion);
        resena.setComentario(comentario);
        resena.setFecha(fecha);
        uow.resenaDAO().insertar(resena);
    }
}
