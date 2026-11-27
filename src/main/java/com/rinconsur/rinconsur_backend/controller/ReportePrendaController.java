package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;
import com.rinconsur.rinconsur_backend.model.ReportePrenda;
import com.rinconsur.rinconsur_backend.model.Probador;
import com.rinconsur.rinconsur_backend.model.EstadoProbador;
import com.rinconsur.rinconsur_backend.repository.ReportePrendaRepository;
import com.rinconsur.rinconsur_backend.repository.ProbadorRepository;
import com.rinconsur.rinconsur_backend.dto.ReportePrendaRequest;
import com.rinconsur.rinconsur_backend.service.RegistroOcupacionService;
import com.rinconsur.rinconsur_backend.service.FotoReferenciaService;
import com.rinconsur.rinconsur_backend.service.FotoLimpiezaService;

import jakarta.validation.Valid;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/reportes-prenda")
public class ReportePrendaController {

    private final ReportePrendaRepository reporteRepository;
    private final ProbadorRepository probadorRepository;
    private final RegistroOcupacionService registroService;
    private final FotoReferenciaService fotoReferenciaService;
    private final FotoLimpiezaService fotoLimpiezaService;

    public ReportePrendaController(
            ReportePrendaRepository reporteRepository,
            ProbadorRepository probadorRepository,
            RegistroOcupacionService registroService,
            FotoReferenciaService fotoReferenciaService,
            FotoLimpiezaService fotoLimpiezaService) {

        this.reporteRepository = reporteRepository;
        this.probadorRepository = probadorRepository;
        this.registroService = registroService;
        this.fotoReferenciaService = fotoReferenciaService;
        this.fotoLimpiezaService = fotoLimpiezaService;
    }

    // Buscar reporte o generar error 404
    private ReportePrenda obtenerReporte(Integer id) {
        return reporteRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Reporte no encontrado"));
    }

    @GetMapping
    public List<ReportePrenda> listar() {
        return reporteRepository
                .findAllByOrderByFechaMillisDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportePrenda> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(obtenerReporte(id));
    }

    @GetMapping("/probador/{probadorId}")
    public List<ReportePrenda> listarPorProbador(
            @PathVariable Integer probadorId) {

        return reporteRepository
                .findByProbadorIdOrderByFechaMillisDesc(probadorId);
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

        String fotoUri = fotoReferenciaService
                .validar(request.fotoUri());

        if (probador.getEstado() == EstadoProbador.OCUPADO
                && probador.getInicioOcupacion() != null) {

            registroService.registrarFinOcupacion(
                    probador,
                    true,
                    "Prenda olvidada en el probador"
            );

            probador.setEstado(EstadoProbador.PRENDA_OLVIDADA);
            probador.setInicioOcupacion(null);
            probador.setCantidadPrendas(0);

            probadorRepository.save(probador);
        }

        ReportePrenda reporte = new ReportePrenda();

        reporte.setProbadorId(request.probadorId());
        reporte.setDescripcion(request.descripcion().trim());
        reporte.setFotoUri(fotoUri);
        reporte.setFechaMillis(System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reporteRepository.save(reporte));
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<ReportePrenda> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ReportePrendaRequest request) {

        ReportePrenda reporte = obtenerReporte(id);

        if (!probadorRepository.existsById(request.probadorId())) {
            return ResponseEntity.badRequest().build();
        }

        String fotoAnterior = reporte.getFotoUri();

        String fotoNueva = fotoReferenciaService
                .validar(request.fotoUri());

        reporte.setProbadorId(request.probadorId());
        reporte.setDescripcion(request.descripcion().trim());
        reporte.setFotoUri(fotoNueva);

        ReportePrenda actualizado =
                reporteRepository.save(reporte);

        if (!Objects.equals(fotoAnterior, fotoNueva)) {
            fotoLimpiezaService
                    .limpiarDespuesDeCommit(fotoAnterior);
        }

        return ResponseEntity.ok(actualizado);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id) {

        ReportePrenda reporte = obtenerReporte(id);

        String fotoAnterior = reporte.getFotoUri();

        reporteRepository.delete(reporte);

        fotoLimpiezaService
                .limpiarDespuesDeCommit(fotoAnterior);

        return ResponseEntity.noContent().build();
    }
}