
package com.rinconsur.rinconsur_backend.repository;

import com.rinconsur.rinconsur_backend.model.Probador;
import com.rinconsur.rinconsur_backend.model.EstadoProbador;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProbadorRepository
        extends JpaRepository<Probador, Integer> {

    List<Probador> findBySedeIdOrderByNumeroAsc(Integer sedeId);

    Optional<Probador> findBySedeIdAndNumero(
            Integer sedeId, Integer numero);

    boolean existsBySedeIdAndNumero(
            Integer sedeId, Integer numero);

    boolean existsBySedeId(Integer sedeId);

    List<Probador> findBySedeIdAndEstado(
            Integer sedeId, EstadoProbador estado);
}
