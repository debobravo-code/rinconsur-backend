package com.rinconsur.rinconsur_backend.dto;

import com.rinconsur.rinconsur_backend.model.Probador;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Datos que se pueden corregir en un registro del historial. */
public record RegistroEdicionRequest(

    @NotNull(message = "La cantidad de prendas es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    @Max(value = Probador.MAX_PRENDAS, message = "Maximo 4 prendas")
    Integer cantidadPrendas,

    boolean prendaOlvidada,

    String observacion

) {
}
