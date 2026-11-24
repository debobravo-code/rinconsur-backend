
package com.rinconsur.rinconsur_backend.repository;

import com.rinconsur.rinconsur_backend.model.RegistroOcupacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroOcupacionRepository
        extends JpaRepository<RegistroOcupacion, Integer> {

    List<RegistroOcupacion> findAllByOrderByFinMillisDesc();

    List<RegistroOcupacion> findBySedeIdOrderByFinMillisDesc(
            Integer sedeId);

    List<RegistroOcupacion> findBySedeIdAndNumeroProbadorOrderByFinMillisDesc(
            Integer sedeId, Integer numeroProbador);

    List<RegistroOcupacion> findBySedeIdAndPrendaOlvidadaOrderByFinMillisDesc(
            Integer sedeId, boolean prendaOlvidada);

    List<RegistroOcupacion> findBySedeIdAndFinMillisBetweenOrderByFinMillisDesc(
            Integer sedeId, Long desde, Long hasta);
}
