
package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.model.ReportePrenda;
import com.rinconsur.rinconsur_backend.model.Probador;
import com.rinconsur.rinconsur_backend.model.EstadoProbador;
import com.rinconsur.rinconsur_backend.repository.ReportePrendaRepository;
import com.rinconsur.rinconsur_backend.repository.ProbadorRepository;
import com.rinconsur.rinconsur_backend.dto.ReportePrendaRequest;
import com.rinconsur.rinconsur_backend.service.RegistroOcupacionService;

import jakarta.validation.Valid;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes-prenda")
public class ReportePrendaController {

    private final ReportePrendaRepository reporteRepository;
    private final ProbadorRepository probadorRepository;
    private final RegistroOcupacionService registroService;

    public ReportePrendaController(
            ReportePrendaRepository reporteRepository,
            ProbadorRepository probadorRepository,
            RegistroOcupacionService registroService) {

        this.reporteRepository = reporteRepository;
        this.probadorRepository = probadorRepository;
        this.registroService = registroService;
    }

    @GetMapping
    public List<ReportePrenda> listar() {
        return reporteRepository
                .findAllByOrderByFechaMillisDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportePrenda> buscarPorId(
            @PathVariable Integer id) {

        return reporteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/probador/{probadorId}")
    public List<ReportePrenda> listarPorProbador(
            @PathVariable Integer probadorId) {

        return reporteRepository
                .findByProbadorIdOrderByFechaMillisDesc(
                        probadorId);
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<ReportePrenda>> listarPorFechas(
            @RequestParam Long desde,
            @RequestParam Long hasta) {

        if (desde < 0 || desde > hasta) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                reporteRepository
                        .findByFechaMillisBetweenOrderByFechaMillisDesc(
                                desde, hasta));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<ReportePrenda> crear(
            @Valid @RequestBody ReportePrendaRequest request) {

        Probador probador = probadorRepository
                .findById(request.probadorId())
                .orElse(null);

        if (probador == null) {
            return ResponseEntity.badRequest().build();
        }

        if (probador.getEstado() == EstadoProbador.OCUPADO
                && probador.getInicioOcupacion() != null) {

            registroService.registrarFinOcupacion(
                    probador,
                    true,
                    "Prenda olvidada en el probador"
            );

            probador.setEstado(
                    EstadoProbador.PRENDA_OLVIDADA);

            probador.setInicioOcupacion(null);
            probador.setCantidadPrendas(0);

            probadorRepository.save(probador);
        }

        ReportePrenda reporte = new ReportePrenda();

        reporte.setProbadorId(request.probadorId());
        reporte.setDescripcion(request.descripcion().trim());
        reporte.setFotoUri(request.fotoUri());
        reporte.setFechaMillis(System.currentTimeMillis());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reporteRepository.save(reporte));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportePrenda> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ReportePrendaRequest request) {

        ReportePrenda reporte = reporteRepository
                .findById(id)
                .orElse(null);

        if (reporte == null) {
            return ResponseEntity.notFound().build();
        }

        if (!probadorRepository.existsById(
                request.probadorId())) {

            return ResponseEntity.badRequest().build();
        }

        reporte.setProbadorId(request.probadorId());
        reporte.setDescripcion(request.descripcion().trim());
        reporte.setFotoUri(request.fotoUri());

        return ResponseEntity.ok(
                reporteRepository.save(reporte));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id) {

        if (!reporteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        reporteRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
