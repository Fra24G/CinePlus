package com.cineplus.entidades;

import com.cineplus.entidades.enums.EstadoPago;
import com.cineplus.entidades.enums.MetodoPago;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/**
 * Compra de boletos de un usuario para una funcion, con el detalle del cobro y
 * de los puntos.
 *
 * @author luisf
 */
@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_funcion", nullable = false)
    private Funcion funcion;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "subtotal_boletos", nullable = false)
    private Double subtotalBoletos = 0.0;

    @Column(name = "cargo_servicio", nullable = false)
    private Double cargoServicio = 0.0;

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal = 0.0;

    @Column(name = "puntos_usados", nullable = false)
    private Integer puntosUsados = 0;

    @Column(name = "descuento_puntos", nullable = false)
    private Double descuentoPuntos = 0.0;

    @Column(name = "monto_cobrado", nullable = false)
    private Double montoCobrado = 0.0;

    @Column(name = "puntos_ganados", nullable = false)
    private Integer puntosGanados = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", length = 30)
    private MetodoPago metodoPago;

    // Solo los ultimos 4 digitos de la tarjeta. Nunca se guarda el numero completo
    @Column(name = "ultimos_digitos", length = 4)
    private String ultimosDigitos;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago", nullable = false, length = 20)
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    @Column(name = "motivo_cancelacion", length = 300)
    private String motivoCancelacion;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    // Lista de textos: se guarda en una tabla aparte llamada compra_productos
    @ElementCollection
    @CollectionTable(name = "compra_productos",
            joinColumns = @JoinColumn(name = "id_compra"))
    @Column(name = "producto", length = 100)
    private List<String> listaDeProductos = new ArrayList<>();

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Boleto> boletos = new ArrayList<>();

    public Compra() {
    }

    /**
     * Agrega un boleto a la compra y le asigna esta compra.
     */
    public void agregarBoleto(Boleto boleto) {
        boleto.setCompra(this);
        boletos.add(boleto);
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public void setFuncion(Funcion funcion) {
        this.funcion = funcion;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Double getSubtotalBoletos() {
        return subtotalBoletos;
    }

    public void setSubtotalBoletos(Double subtotalBoletos) {
        this.subtotalBoletos = subtotalBoletos;
    }

    public Double getCargoServicio() {
        return cargoServicio;
    }

    public void setCargoServicio(Double cargoServicio) {
        this.cargoServicio = cargoServicio;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Integer getPuntosUsados() {
        return puntosUsados;
    }

    public void setPuntosUsados(Integer puntosUsados) {
        this.puntosUsados = puntosUsados;
    }

    public Double getDescuentoPuntos() {
        return descuentoPuntos;
    }

    public void setDescuentoPuntos(Double descuentoPuntos) {
        this.descuentoPuntos = descuentoPuntos;
    }

    public Double getMontoCobrado() {
        return montoCobrado;
    }

    public void setMontoCobrado(Double montoCobrado) {
        this.montoCobrado = montoCobrado;
    }

    public Integer getPuntosGanados() {
        return puntosGanados;
    }

    public void setPuntosGanados(Integer puntosGanados) {
        this.puntosGanados = puntosGanados;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getUltimosDigitos() {
        return ultimosDigitos;
    }

    public void setUltimosDigitos(String ultimosDigitos) {
        this.ultimosDigitos = ultimosDigitos;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }

    public LocalDateTime getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDateTime fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public List<String> getListaDeProductos() {
        return listaDeProductos;
    }

    public void setListaDeProductos(List<String> listaDeProductos) {
        this.listaDeProductos = listaDeProductos;
    }

    public List<Boleto> getBoletos() {
        return boletos;
    }

    public void setBoletos(List<Boleto> boletos) {
        this.boletos = boletos;
    }
}
