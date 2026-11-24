
package com.rinconsur.rinconsur_backend.repository;

import com.rinconsur.rinconsur_backend.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Integer> {

    Optional<TipoUsuario> findByTipoUsuario(String tipoUsuario);

    boolean existsByTipoUsuario(String tipoUsuario);
}
