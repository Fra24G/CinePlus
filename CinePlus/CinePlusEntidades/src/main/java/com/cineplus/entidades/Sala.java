package com.cineplus.entidades;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

/**
 * Sala de un cine, con sus asientos.
 *
 * @author luisf
 */
@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Integer idSala;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cine", nullable = false)
    private Cine cine;

    @Column(name = "numero_sala", nullable = false)
    private Integer numeroSala;

    @Column(name = "capacidad_total", nullable = false)
    private Integer capacidadTotal;

    @Column(name = "tipo_sala", length = 30)
    private String tipoSala;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asiento> asientos = new ArrayList<>();

    public Sala() {
    }

    /**
     * Agrega un asiento a la sala y le asigna esta sala.
     */
    public void agregarAsiento(Asiento asiento) {
        asiento.setSala(this);
        asientos.add(asiento);
    }

    public Integer getIdSala() {
        return idSala;
    }

    public void setIdSala(Integer idSala) {
        this.idSala = idSala;
    }

    public Cine getCine() {
        return cine;
    }

    public void setCine(Cine cine) {
        this.cine = cine;
    }

    public Integer getNumeroSala() {
        return numeroSala;
    }

    public void setNumeroSala(Integer numeroSala) {
        this.numeroSala = numeroSala;
    }

    public Integer getCapacidadTotal() {
        return capacidadTotal;
    }

    public void setCapacidadTotal(Integer capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
    }

    public String getTipoSala() {
        return tipoSala;
    }

    public void setTipoSala(String tipoSala) {
        this.tipoSala = tipoSala;
    }

    public List<Asiento> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<Asiento> asientos) {
        this.asientos = asientos;
    }
}
