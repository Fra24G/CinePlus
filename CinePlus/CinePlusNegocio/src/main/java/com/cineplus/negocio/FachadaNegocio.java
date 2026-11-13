package com.cineplus.negocio;

import com.cineplus.cineplusservicioqr.GeneradorQR;
import com.cineplus.cineplusservicioqr.IGeneradorQR;
import com.cineplus.dto.ActualizarPerfilDTO;
import com.cineplus.dto.AsientoDTO;
import com.cineplus.dto.BoletoDTO;
import com.cineplus.dto.CancelacionDTO;
import com.cineplus.dto.CarteleraDTO;
import com.cineplus.dto.CineDTO;
import com.cineplus.dto.CompraDTO;
import com.cineplus.dto.CotizacionDTO;
import com.cineplus.dto.CredencialesDTO;
import com.cineplus.dto.DatosPagoDTO;
import com.cineplus.dto.FiltroComprasDTO;
import com.cineplus.dto.FuncionDTO;
import com.cineplus.dto.GuardarFuncionDTO;
import com.cineplus.dto.MembresiaDTO;
import com.cineplus.dto.NuevaResenaDTO;
import com.cineplus.dto.PaginaDTO;
import com.cineplus.dto.PeliculaDTO;
import com.cineplus.dto.PerfilDTO;
import com.cineplus.dto.PlanDTO;
import com.cineplus.dto.RegistroUsuarioDTO;
import com.cineplus.dto.ResenaDTO;
import com.cineplus.dto.ResumenAdminDTO;
import com.cineplus.dto.SalaDTO;
import com.cineplus.dto.SesionDTO;
import com.cineplus.dto.SolicitudCompraDTO;
import com.cineplus.dto.TransaccionDTO;
import com.cineplus.dto.UsuarioDTO;
import com.cineplus.negocio.bo.AdminBO;
import com.cineplus.negocio.bo.BoletoBO;
import com.cineplus.negocio.bo.CarteleraBO;
import com.cineplus.negocio.bo.CineBO;
import com.cineplus.negocio.bo.CompraBO;
import com.cineplus.negocio.bo.FuncionBO;
import com.cineplus.negocio.bo.HistorialBO;
import com.cineplus.negocio.bo.MembresiaBO;
import com.cineplus.negocio.bo.PeliculaBO;
import com.cineplus.negocio.bo.ResenaBO;
import com.cineplus.negocio.bo.UsuarioBO;
import com.cineplus.negocio.carga.CargaInicial;
import com.cineplus.negocio.control.ControlCompra;
import com.cineplus.negocio.control.ControlMembresia;
import com.cineplus.negocio.control.ControlRegistro;
import com.cineplus.negocio.control.ControlUsuario;
import com.cineplus.servicio.IServicioPeliculas;
import com.cineplus.servicio.ServicioPeliculasTmdb;
import java.util.List;
import java.util.Optional;

/**
 * PATRON FACHADA.
 * Unica puerta de entrada a la logica de negocio: Presentacion solo conoce esta clase
 * y los DTOs. Por dentro coordina los controles y los objetos de negocio.
 * Cada metodo toma al usuario de la sesion (nunca de la pantalla) y los metodos
 * de administracion exigen el rol ADMIN.
 * Queda cerrada despues del Flujo 0: un metodo nuevo se pide al Desarrollador 1.
 *
 * @author luisf
 */
public class FachadaNegocio {

    private final IServicioPeliculas servicioPeliculas;
    private final UsuarioBO usuarioBO;
    private final ControlRegistro registro;
    private final ControlUsuario usuario;
    private final CarteleraBO cartelera;
    private final PeliculaBO peliculaBO;
    private final CineBO cineBO;
    private final FuncionBO funcionBO;
    private final CompraBO compraBO;
    private final BoletoBO boletoBO;
    private final HistorialBO historialBO;
    private final MembresiaBO membresiaBO;
    private final ResenaBO resenaBO;
    private final AdminBO adminBO;
    private final ControlCompra controlCompra;
    private final ControlMembresia controlMembresia;
    private final CargaInicial cargaInicial;

    /** Fachada normal: usa TMDB como servicio de peliculas y el generador de QR. */
    public FachadaNegocio() {
        this(new ServicioPeliculasTmdb(), new GeneradorQR());
    }

    /** Inyeccion por constructor: los servicios externos se pueden cambiar (por ejemplo, para pruebas). */
    public FachadaNegocio(IServicioPeliculas servicioPeliculas, IGeneradorQR generadorQR) {
        this.servicioPeliculas = servicioPeliculas;
        this.usuarioBO = new UsuarioBO();
        this.registro = new ControlRegistro(usuarioBO);
        this.usuario = new ControlUsuario(usuarioBO);
        this.cartelera = new CarteleraBO(servicioPeliculas);
        this.peliculaBO = new PeliculaBO(servicioPeliculas);
        this.cineBO = new CineBO();
        this.funcionBO = new FuncionBO();
        this.compraBO = new CompraBO();
        this.boletoBO = new BoletoBO(generadorQR);
        this.historialBO = new HistorialBO();
        this.membresiaBO = new MembresiaBO();
        this.resenaBO = new ResenaBO(peliculaBO);
        this.adminBO = new AdminBO();
        this.controlCompra = new ControlCompra(compraBO, membresiaBO, usuarioBO, usuario);
        this.controlMembresia = new ControlMembresia(membresiaBO, usuarioBO, usuario);
        this.cargaInicial = new CargaInicial();
    }

    // ---------- Arranque ----------

    /** Carga los datos semilla si la base de datos esta vacia. */
    public void inicializarDatos() throws Exception {
        cargaInicial.ejecutar();
    }

    // ---------- Cuenta y sesion ----------

    /** Crea la cuenta y deja la sesion abierta. */
    public SesionDTO registrarCuenta(RegistroUsuarioDTO datos) throws Exception {
        usuario.cerrarSesion();
        return usuario.abrirSesion(registro.registrarCuenta(datos));
    }

    public SesionDTO autenticar(CredencialesDTO datos) throws Exception {
        return usuario.autenticar(datos);
    }

    public SesionDTO obtenerSesion() {
        return usuario.obtenerSesion();
    }

    public void cerrarSesion() {
        usuario.cerrarSesion();
    }

    // ---------- Catalogo y cartelera ----------

    /** Peliculas generales de TMDB para el carrusel de inicio. */
    public CarteleraDTO consultarCartelera() {
        return cartelera.consultar();
    }

    public byte[] obtenerPoster(PeliculaDTO pelicula) {
        return cartelera.obtenerPoster(pelicula);
    }

    /** Peliculas de la BD en cartelera hoy. */
    public List<PeliculaDTO> consultarCarteleraLocal() throws Exception {
        return peliculaBO.listarCartelera();
    }

    /** La pelicula de ese idExterno si hoy esta en cartelera (clic del carrusel). */
    public Optional<PeliculaDTO> buscarEnCartelera(String idExterno) throws Exception {
        return peliculaBO.buscarEnCartelera(idExterno);
    }

    /** La pelicula de ese idExterno si existe en el catalogo, este o no en cartelera. */
    public Optional<PeliculaDTO> buscarEnCatalogo(String idExterno) throws Exception {
        return peliculaBO.buscarPorIdExterno(idExterno);
    }

    /** Busqueda en TMDB (para "Anadir a cartelera"). */
    public List<PeliculaDTO> buscarPeliculas(String texto) throws Exception {
        return cartelera.buscar(texto);
    }

    // ---------- Funciones y asientos ----------

    public List<CineDTO> listarCines() throws Exception {
        return cineBO.listarCines();
    }

    /** Funciones futuras de la pelicula en ese cine. */
    public List<FuncionDTO> listarFunciones(Integer idPelicula, Integer idCine) throws Exception {
        return funcionBO.listarPorPelicula(idPelicula, idCine);
    }

    /** Mapa de asientos con ocupados y precio. */
    public List<AsientoDTO> listarAsientos(Integer idFuncion) throws Exception {
        return funcionBO.listarAsientos(idFuncion);
    }

    // ---------- Compra ----------

    public CotizacionDTO cotizar(SolicitudCompraDTO solicitud) throws Exception {
        return controlCompra.cotizar(solicitud);
    }

    public CompraDTO comprar(SolicitudCompraDTO solicitud) throws Exception {
        return controlCompra.confirmar(solicitud);
    }

    // ---------- Boletos, historial y cancelacion ----------

    public PaginaDTO<BoletoDTO> listarBoletos(int pagina, int tamano) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        return boletoBO.listarBoletos(actual.idUsuario(), pagina, tamano);
    }

    public byte[] generarQR(BoletoDTO boleto) throws Exception {
        usuario.exigirSesion();
        return boletoBO.generarQR(boleto);
    }

    public PaginaDTO<CompraDTO> consultarHistorial(FiltroComprasDTO filtro) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        return historialBO.consultarHistorial(actual.idUsuario(), filtro);
    }

    public CompraDTO obtenerCompra(Integer idCompra) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        return historialBO.obtener(actual.idUsuario(), idCompra);
    }

    /** Cancela la compra y refresca la sesion (los puntos cambian). */
    public CompraDTO cancelarCompra(CancelacionDTO datos) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        CompraDTO compra = historialBO.cancelar(actual.idUsuario(), datos);
        usuario.refrescar(usuarioBO.obtener(actual.idUsuario()));
        return compra;
    }

    // ---------- Membresia y perfil ----------

    public List<PlanDTO> listarPlanes() {
        return membresiaBO.listarPlanes();
    }

    public PerfilDTO obtenerPerfil() throws Exception {
        return controlMembresia.obtenerPerfil();
    }

    public MembresiaDTO contratarPro(DatosPagoDTO pago) throws Exception {
        return controlMembresia.contratarPro(pago);
    }

    /** Actualiza el perfil y refresca la sesion. */
    public UsuarioDTO actualizarPerfil(ActualizarPerfilDTO datos) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        UsuarioDTO actualizado = usuarioBO.actualizarPerfil(actual.idUsuario(), datos);
        usuario.refrescar(actualizado);
        return actualizado;
    }

    // ---------- Comunidad ----------

    public PaginaDTO<ResenaDTO> listarResenasRecientes(int pagina, int tamano) throws Exception {
        return resenaBO.listarRecientes(pagina, tamano);
    }

    public PaginaDTO<ResenaDTO> listarResenas(Integer idPelicula, int pagina, int tamano) throws Exception {
        return resenaBO.listarPorPelicula(idPelicula, pagina, tamano);
    }

    public ResenaDTO publicarResena(NuevaResenaDTO datos) throws Exception {
        UsuarioDTO actual = usuario.exigirSesion();
        return resenaBO.publicar(actual.idUsuario(), datos);
    }

    // ---------- Administracion (todas exigen rol ADMIN) ----------

    public ResumenAdminDTO obtenerResumenAdmin() throws Exception {
        usuario.exigirAdmin();
        return adminBO.resumenSemana();
    }

    public List<PeliculaDTO> listarPeliculasAdmin() throws Exception {
        usuario.exigirAdmin();
        return peliculaBO.listarTodas();
    }

    public PeliculaDTO guardarPelicula(PeliculaDTO datos) throws Exception {
        usuario.exigirAdmin();
        return peliculaBO.guardar(datos);
    }

    public PeliculaDTO importarPelicula(String idExterno) throws Exception {
        usuario.exigirAdmin();
        return peliculaBO.importar(idExterno, true);
    }

    public void cambiarDisponibilidad(Integer idPelicula, boolean disponible) throws Exception {
        usuario.exigirAdmin();
        peliculaBO.cambiarDisponibilidad(idPelicula, disponible);
    }

    /** Guarda el poster que sube el admin y devuelve la ruta "archivo:..." para PeliculaDTO.imagen. */
    public String subirPoster(byte[] imagen, String nombreArchivo) throws Exception {
        usuario.exigirAdmin();
        return servicioPeliculas.guardarPosterLocal(imagen, nombreArchivo);
    }

    public List<SalaDTO> listarSalas(Integer idCine) throws Exception {
        usuario.exigirAdmin();
        return cineBO.listarSalas(idCine);
    }

    public List<FuncionDTO> listarFuncionesAdmin(Integer idPelicula) throws Exception {
        usuario.exigirAdmin();
        return funcionBO.listarPorPeliculaAdmin(idPelicula);
    }

    public FuncionDTO guardarFuncion(GuardarFuncionDTO datos) throws Exception {
        usuario.exigirAdmin();
        return funcionBO.guardar(datos);
    }

    public void eliminarFuncion(Integer idFuncion) throws Exception {
        usuario.exigirAdmin();
        funcionBO.eliminar(idFuncion);
    }

    public PaginaDTO<TransaccionDTO> listarTransacciones(FiltroComprasDTO filtro) throws Exception {
        usuario.exigirAdmin();
        return historialBO.listarTransacciones(filtro);
    }
}
