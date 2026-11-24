
package com.rinconsur.rinconsur_backend.service;

import com.rinconsur.rinconsur_backend.model.Probador;
import com.rinconsur.rinconsur_backend.model.EstadoProbador;
import com.rinconsur.rinconsur_backend.model.RegistroOcupacion;
import com.rinconsur.rinconsur_backend.repository.RegistroOcupacionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroOcupacionService {

    private final RegistroOcupacionRepository registroRepository;

    public RegistroOcupacionService(
            RegistroOcupacionRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @Transactional
    public RegistroOcupacion registrarFinOcupacion(
            Probador probador,
            boolean prendaOlvidada,
            String observacion) {

        if (probador.getEstado() != EstadoProbador.OCUPADO
                || probador.getInicioOcupacion() == null) {
            throw new IllegalStateException(
                    "El probador no tiene una ocupacion activa");
        }

        RegistroOcupacion registro = new RegistroOcupacion();

        registro.setSedeId(probador.getSedeId());
        registro.setNumeroProbador(probador.getNumero());
        registro.setInicioMillis(probador.getInicioOcupacion());
        registro.setFinMillis(System.currentTimeMillis());
        registro.setPrendaOlvidada(prendaOlvidada);
        registro.setCantidadPrendas(probador.getCantidadPrendas());
        registro.setObservacion(
                observacion == null ? "" : observacion);

        return registroRepository.save(registro);
    }
}
