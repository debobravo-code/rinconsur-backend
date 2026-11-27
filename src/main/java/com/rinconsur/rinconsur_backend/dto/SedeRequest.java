package com.rinconsur.rinconsur_backend.dto;

import com.rinconsur.rinconsur_backend.model.Sede;
import jakarta.validation.constraints.*;

public record SedeRequest(

    @NotBlank
    @Size(max = Sede.MAX_LARGO)
    String nombre,

    @NotNull
    @Min(Sede.ALERTA_MIN)
    @Max(Sede.ALERTA_MAX)
    Integer tiempoAlerta,

    @NotNull
    @Min(1)
    @Max(20)
    Integer cantidadProbadores

) {
}