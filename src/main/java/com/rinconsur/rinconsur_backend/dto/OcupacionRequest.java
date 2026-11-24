
package com.rinconsur.rinconsur_backend.dto;

import com.rinconsur.rinconsur_backend.model.Probador;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record OcupacionRequest(

    @NotNull(message = "La cantidad de prendas es obligatoria")
    @Min(value = 1, message = "Debe ingresar al menos una prenda")
    @Max(value = Probador.MAX_PRENDAS, message = "Maximo 4 prendas")
    Integer cantidadPrendas

) {
}
