
package com.rinconsur.rinconsur_backend.repository;

import com.rinconsur.rinconsur_backend.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SedeRepository extends JpaRepository<Sede, Integer> {

    Optional<Sede> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
