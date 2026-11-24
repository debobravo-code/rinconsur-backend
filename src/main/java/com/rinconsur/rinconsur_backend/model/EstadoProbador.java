
package com.rinconsur.rinconsur_backend.model;

public enum EstadoProbador {

    LIBRE("Libre"),
    OCUPADO("Ocupado"),
    PRENDA_OLVIDADA("Prenda olvidada"),
    FUERA_DE_SERVICIO("Fuera de servicio");

    private final String etiqueta;

    EstadoProbador(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
