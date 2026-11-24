
package com.rinconsur.rinconsur_backend.config;

import com.rinconsur.rinconsur_backend.model.TipoUsuario;
import com.rinconsur.rinconsur_backend.repository.TipoUsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner inicializarDatos(TipoUsuarioRepository repository) {
        return args -> {

            if (repository.count() == 0) {
                repository.save(new TipoUsuario("Administrador"));
            }

            boolean administradorValido = repository
                    .findById(TipoUsuario.ID_ADMINISTRADOR)
                    .map(tipo -> "Administrador".equals(tipo.getTipoUsuario()))
                    .orElse(false);

            if (!administradorValido) {
                throw new IllegalStateException(
                        "El tipo Administrador debe tener ID 1"
                );
            }
        };
    }
}
