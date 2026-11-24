
package com.rinconsur.rinconsur_backend.repository;

import com.rinconsur.rinconsur_backend.model.ReportePrenda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportePrendaRepository
        extends JpaRepository<ReportePrenda, Integer> {

    List<ReportePrenda> findAllByOrderByFechaMillisDesc();

    List<ReportePrenda>
        findByProbadorIdOrderByFechaMillisDesc(
            Integer probadorId);

    boolean existsByProbadorId(Integer probadorId);

    List<ReportePrenda>
        findByFechaMillisBetweenOrderByFechaMillisDesc(
            Long desde, Long hasta);

    boolean existsByFotoUri(String fotoUri);
}
