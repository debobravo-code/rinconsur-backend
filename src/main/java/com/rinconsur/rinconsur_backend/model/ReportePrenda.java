
package com.rinconsur.rinconsur_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
    name = "reportes_prenda",
    indexes = {
        @Index(
            name = "idx_reporte_probador",
            columnList = "probador_id"
        )
    }
)
public class ReportePrenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El probador es obligatorio")
    @Column(name = "probador_id", nullable = false)
    private Integer probadorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "probador_id",
        insertable = false,
        updatable = false,
        foreignKey = @ForeignKey(
            name = "fk_reporte_probador"
        )
    )
    @JsonIgnore
    private Probador probador;

    @NotBlank(message = "La descripcion es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "foto_uri", columnDefinition = "TEXT")
    private String fotoUri;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha_millis", nullable = false)
    private Long fechaMillis;

    public ReportePrenda() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProbadorId() {
        return probadorId;
    }

    public void setProbadorId(Integer probadorId) {
        this.probadorId = probadorId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    public Long getFechaMillis() {
        return fechaMillis;
    }

    public void setFechaMillis(Long fechaMillis) {
        this.fechaMillis = fechaMillis;
    }
}
