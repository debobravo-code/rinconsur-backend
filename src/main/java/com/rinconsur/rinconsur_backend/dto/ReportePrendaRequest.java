
package com.rinconsur.rinconsur_backend.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ReportePrendaRequest(

    @NotNull(message = "El probador es obligatorio")
    @Positive(message = "El ID del probador debe ser positivo")
    Integer probadorId,

    @NotNull(message = "La descripcion no puede ser nula")
    String descripcion,

    String fotoUri

) {

    @AssertTrue(message = "Debe agregar una descripcion o una fotografia")
    @JsonIgnore
    public boolean isContenidoValido() {

        boolean tieneDescripcion =
                descripcion != null && !descripcion.isBlank();

        boolean tieneFoto =
                fotoUri != null && !fotoUri.isBlank();

        return tieneDescripcion || tieneFoto;
    }
}
