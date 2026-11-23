
package com.rinconsur.rinconsur_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "sedes")
public class Sede {

    public static final int MAX_LARGO = 30;
    public static final int ALERTA_MIN = 5;
    public static final int ALERTA_MAX = 60;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = MAX_LARGO)
    @Column(nullable = false, unique = true, length = MAX_LARGO)
    private String nombre;

    @Min(ALERTA_MIN)
    @Max(ALERTA_MAX)
    @Column(nullable = false)
    private Integer tiempoAlerta = 15;

    public Sede() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getTiempoAlerta() {
        return tiempoAlerta;
    }

    public void setTiempoAlerta(Integer tiempoAlerta) {
        this.tiempoAlerta = tiempoAlerta;
    }
}
