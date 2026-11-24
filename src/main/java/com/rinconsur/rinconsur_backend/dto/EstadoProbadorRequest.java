
package com.rinconsur.rinconsur_backend.dto;

import com.rinconsur.rinconsur_backend.model.EstadoProbador;

import jakarta.validation.constraints.NotNull;

public record EstadoProbadorRequest(

    @NotNull(message = "El estado es obligatorio")
    EstadoProbador estado

) {
}
