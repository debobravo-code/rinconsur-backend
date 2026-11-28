package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.exception.ConflictException;
import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;
import com.rinconsur.rinconsur_backend.model.Probador;
import com.rinconsur.rinconsur_backend.model.EstadoProbador;
import com.rinconsur.rinconsur_backend.repository.ProbadorRepository;
import com.rinconsur.rinconsur_backend.repository.SedeRepository;
import com.rinconsur.rinconsur_backend.repository.ReportePrendaRepository;
import com.rinconsur.rinconsur_backend.dto.OcupacionRequest;
import com.rinconsur.rinconsur_backend.dto.EstadoProbadorRequest;
import com.rinconsur.rinconsur_backend.service.RegistroOcupacionService;

import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/probadores")
public class ProbadorController {

    private final ProbadorRepository probadorRepository;
    private final SedeRepository sedeRepository;
    private final RegistroOcupacionService registroService;
    private final ReportePrendaRepository reporteRepository;

    public ProbadorController(
            ProbadorRepository probadorRepository,
            SedeRepository sedeRepository,
            RegistroOcupacionService registroService,
            ReportePrendaRepository reporteRepository) {

        this.probadorRepository = probadorRepository;
        this.sedeRepository = sedeRepository;
        this.registroService = registroService;
        this.reporteRepository = reporteRepository;
    }

    // Buscar probador o generar error 404
    private Probador obtenerProbador(Integer id) {
        return probadorRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Probador no encontrado"));
    }

    @GetMapping
    public List<Probador> listar() {
        return probadorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Probador> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(obtenerProbador(id));
    }

    @GetMapping("/sede/{sedeId}")
    public List<Probador> listarPorSede(
            @PathVariable Integer sedeId) {

        return probadorRepository
                .findBySedeIdOrderByNumeroAsc(sedeId);
    }

    @GetMapping("/sede/{sedeId}/estado/{estado}")
    public List<Probador> listarPorEstado(
            @PathVariable Integer sedeId,
            @PathVariable EstadoProbador estado) {

        return probadorRepository
                .findBySedeIdAndEstado(sedeId, estado);
    }

    @PostMapping
    public ResponseEntity<Probador> crear(
            @Valid @RequestBody Probador probador) {

        if (!sedeRepository.existsById(probador.getSedeId())) {
            return ResponseEntity.badRequest().build();
        }

        if (probadorRepository.existsBySedeIdAndNumero(
                probador.getSedeId(), probador.getNumero())) {
            throw new ConflictException("El numero de probador ya existe en esta sede");
        }

        probador.setId(null);
        probador.setEstado(EstadoProbador.LIBRE);
        probador.setInicioOcupacion(null);
        probador.setCantidadPrendas(0);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(probadorRepository.save(probador));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Probador> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody Probador datos) {

        Probador existente = obtenerProbador(id);

        if (!sedeRepository.existsById(datos.getSedeId())) {
            return ResponseEntity.badRequest().build();
        }

        boolean duplicado = probadorRepository
                .findBySedeIdAndNumero(
                        datos.getSedeId(),
                        datos.getNumero())
                .filter(p -> !p.getId().equals(id))
                .isPresent();

        if (duplicado) {
            throw new ConflictException("El numero de probador ya existe en esta sede");
        }

        if (existente.getEstado() != EstadoProbador.LIBRE
                && existente.getEstado() != EstadoProbador.FUERA_DE_SERVICIO) {
            throw new ConflictException(
                    "Solo se pueden modificar probadores libres o fuera de servicio");
        }

        existente.setNumero(datos.getNumero());
        existente.setSedeId(datos.getSedeId());

        return ResponseEntity.ok(probadorRepository.save(existente));
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Integer id) {

        Probador probador = obtenerProbador(id);

        if (probador.getEstado() != EstadoProbador.LIBRE) {
            throw new ConflictException("Solo se pueden eliminar probadores libres");
        }

        if (reporteRepository.existsByProbadorId(id)) {
            throw new ConflictException("El probador tiene reportes asociados");
        }

        probadorRepository.delete(probador);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ocupar")
    public ResponseEntity<Probador> ocupar(
            @PathVariable Integer id,
            @Valid @RequestBody OcupacionRequest request) {

        Probador probador = obtenerProbador(id);

        if (probador.getEstado() != EstadoProbador.LIBRE) {
            throw new ConflictException("El probador no esta disponible");
        }

        probador.setEstado(EstadoProbador.OCUPADO);
        probador.setInicioOcupacion(System.currentTimeMillis());
        probador.setCantidadPrendas(request.cantidadPrendas());

        return ResponseEntity.ok(probadorRepository.save(probador));
    }

    @Transactional
    @PatchMapping("/{id}/liberar")
    public ResponseEntity<Probador> liberar(
            @PathVariable Integer id) {

        Probador probador = obtenerProbador(id);

        if (probador.getEstado() != EstadoProbador.OCUPADO) {
            throw new ConflictException("El probador no esta ocupado");
        }

        registroService.registrarFinOcupacion(
                probador, false, "");

        probador.setEstado(EstadoProbador.LIBRE);
        probador.setInicioOcupacion(null);
        probador.setCantidadPrendas(0);

        return ResponseEntity.ok(probadorRepository.save(probador));
    }

    @Transactional
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Probador> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoProbadorRequest request) {

        Probador probador = obtenerProbador(id);

        EstadoProbador actual = probador.getEstado();
        EstadoProbador nuevo = request.estado();

        boolean permitido = switch (actual) {

            case LIBRE ->
                    nuevo == EstadoProbador.FUERA_DE_SERVICIO
                    || nuevo == EstadoProbador.PRENDA_OLVIDADA;

            case OCUPADO ->
                    nuevo == EstadoProbador.PRENDA_OLVIDADA;

            case PRENDA_OLVIDADA ->
                    nuevo == EstadoProbador.LIBRE;

            case FUERA_DE_SERVICIO ->
                    nuevo == EstadoProbador.LIBRE;
        };

        if (!permitido) {
            throw new ConflictException("Cambio de estado no permitido");
        }

        if (actual == EstadoProbador.OCUPADO
                && nuevo == EstadoProbador.PRENDA_OLVIDADA) {

            registroService.registrarFinOcupacion(
                    probador,
                    true,
                    "Prenda olvidada en el probador");
        }

        probador.setEstado(nuevo);
        probador.setInicioOcupacion(null);
        probador.setCantidadPrendas(0);

        return ResponseEntity.ok(probadorRepository.save(probador));
    }
}