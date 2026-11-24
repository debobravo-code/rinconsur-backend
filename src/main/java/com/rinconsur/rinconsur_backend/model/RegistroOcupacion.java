
package com.rinconsur.rinconsur_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "registros")
public class RegistroOcupacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "sede_id", nullable = false)
    private Integer sedeId;

    @NotNull
    @Min(1)
    @Column(name = "numero_probador", nullable = false)
    private Integer numeroProbador;

    @NotNull
    @Column(name = "inicio_millis", nullable = false)
    private Long inicioMillis;

    @NotNull
    @Column(name = "fin_millis", nullable = false)
    private Long finMillis;

    @Column(name = "prenda_olvidada", nullable = false)
    private boolean prendaOlvidada = false;

    @NotNull
    @Min(0)
    @Max(Probador.MAX_PRENDAS)
    @Column(name = "cantidad_prendas", nullable = false)
    private Integer cantidadPrendas = 0;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String observacion = "";

    public RegistroOcupacion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSedeId() {
        return sedeId;
    }

    public void setSedeId(Integer sedeId) {
        this.sedeId = sedeId;
    }

    public Integer getNumeroProbador() {
        return numeroProbador;
    }

    public void setNumeroProbador(Integer numeroProbador) {
        this.numeroProbador = numeroProbador;
    }

    public Long getInicioMillis() {
        return inicioMillis;
    }

    public void setInicioMillis(Long inicioMillis) {
        this.inicioMillis = inicioMillis;
    }

    public Long getFinMillis() {
        return finMillis;
    }

    public void setFinMillis(Long finMillis) {
        this.finMillis = finMillis;
    }

    public boolean isPrendaOlvidada() {
        return prendaOlvidada;
    }

    public void setPrendaOlvidada(boolean prendaOlvidada) {
        this.prendaOlvidada = prendaOlvidada;
    }

    public Integer getCantidadPrendas() {
        return cantidadPrendas;
    }

    public void setCantidadPrendas(Integer cantidadPrendas) {
        this.cantidadPrendas = cantidadPrendas;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}