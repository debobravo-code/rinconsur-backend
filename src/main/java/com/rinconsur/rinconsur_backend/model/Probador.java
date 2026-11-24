
package com.rinconsur.rinconsur_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
    name = "probadores",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_probador_sede_numero",
            columnNames = {"sede_id", "numero"}
        )
    }
)
public class Probador {

    public static final int MAX_PRENDAS = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El numero es obligatorio")
    @Min(value = 1, message = "El numero debe ser mayor a cero")
    @Column(nullable = false)
    private Integer numero;

    @NotNull(message = "La sede es obligatoria")
    @Column(name = "sede_id", nullable = false)
    private Integer sedeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "sede_id",
        insertable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_probador_sede")
    )
    @JsonIgnore
    private Sede sede;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoProbador estado = EstadoProbador.LIBRE;

    @Column(name = "inicio_ocupacion")
    private Long inicioOcupacion;

    @NotNull(message = "La cantidad de prendas es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Max(value = MAX_PRENDAS, message = "Maximo 4 prendas")
    @Column(name = "cantidad_prendas", nullable = false)
    private Integer cantidadPrendas = 0;

    public Probador() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getSedeId() {
        return sedeId;
    }

    public void setSedeId(Integer sedeId) {
        this.sedeId = sedeId;
    }

    public EstadoProbador getEstado() {
        return estado;
    }

    public void setEstado(EstadoProbador estado) {
        this.estado = estado;
    }

    public Long getInicioOcupacion() {
        return inicioOcupacion;
    }

    public void setInicioOcupacion(Long inicioOcupacion) {
        this.inicioOcupacion = inicioOcupacion;
    }

    public Integer getCantidadPrendas() {
        return cantidadPrendas;
    }

    public void setCantidadPrendas(Integer cantidadPrendas) {
        this.cantidadPrendas = cantidadPrendas;
    }
}
