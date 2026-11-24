
package com.rinconsur.rinconsur_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReportePrendaRequest(

    @NotNull(message = "El probador es obligatorio")
    @Positive(message = "El ID del probador debe ser positivo")
    Integer probadorId,

    @NotBlank(message = "La descripcion es obligatoria")
    String descripcion,

    String fotoUri

) {
}
