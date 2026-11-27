package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.dto.SedeRequest;
import com.rinconsur.rinconsur_backend.exception.ConflictException;
import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;
import com.rinconsur.rinconsur_backend.model.Sede;
import com.rinconsur.rinconsur_backend.model.Probador;
import com.rinconsur.rinconsur_backend.repository.SedeRepository;
import com.rinconsur.rinconsur_backend.repository.ProbadorRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeRepository sedeRepository;
    private final ProbadorRepository probadorRepository;

    public SedeController(
            SedeRepository sedeRepository,
            ProbadorRepository probadorRepository) {

        this.sedeRepository = sedeRepository;
        this.probadorRepository = probadorRepository;
    }

    // GET: obtener todas las sedes
    @GetMapping
    public List<Sede> listarSedes() {
        return sedeRepository.findAll();
    }

    // GET: buscar una sede por ID
    @GetMapping("/{id}")
    public ResponseEntity<Sede> buscarSede(@PathVariable Integer id) {

        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Sede no encontrada"));

        return ResponseEntity.ok(sede);
    }

    // POST: registrar sede y crear sus probadores
    @Transactional
    @PostMapping
    public ResponseEntity<Sede> crearSede(
            @Valid @RequestBody SedeRequest datos) {

        if (sedeRepository.existsByNombre(datos.nombre())) {
            throw new ConflictException("Ya existe una sede con ese nombre");
        }

        Sede sede = new Sede();
        sede.setNombre(datos.nombre());
        sede.setTiempoAlerta(datos.tiempoAlerta());

        Sede nuevaSede = sedeRepository.save(sede);

        crearProbadores(
                nuevaSede.getId(),
                1,
                datos.cantidadProbadores());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevaSede);
    }

    // PUT: actualizar sede y aumentar sus probadores
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<Sede> actualizarSede(
            @PathVariable Integer id,
            @Valid @RequestBody SedeRequest datos) {

        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Sede no encontrada"));

        if (!sede.getNombre().equals(datos.nombre())
                && sedeRepository.existsByNombre(datos.nombre())) {
            throw new ConflictException("Ya existe una sede con ese nombre");
        }

        List<Probador> probadores =
                probadorRepository.findBySedeIdOrderByNumeroAsc(id);

        int cantidadActual = probadores.size();

        if (datos.cantidadProbadores() < cantidadActual) {
            throw new ConflictException(
                    "No se puede reducir la cantidad de probadores");
        }

        sede.setNombre(datos.nombre());
        sede.setTiempoAlerta(datos.tiempoAlerta());

        Sede sedeActualizada = sedeRepository.save(sede);

        int cantidadNuevos =
                datos.cantidadProbadores() - cantidadActual;

        int ultimoNumero = probadores.stream()
                .mapToInt(Probador::getNumero)
                .max()
                .orElse(0);

        crearProbadores(
                id,
                ultimoNumero + 1,
                cantidadNuevos);

        return ResponseEntity.ok(sedeActualizada);
    }

    // DELETE: eliminar una sede
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSede(@PathVariable Integer id) {

        if (!sedeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sede no encontrada");
        }

        if (probadorRepository.existsBySedeId(id)) {
            throw new ConflictException(
                    "No se puede eliminar una sede que tiene probadores");
        }

        sedeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // Metodo auxiliar para crear probadores
    private void crearProbadores(
            Integer sedeId,
            int numeroInicial,
            int cantidad) {

        for (int i = 0; i < cantidad; i++) {

            Probador probador = new Probador();

            probador.setNumero(numeroInicial + i);
            probador.setSedeId(sedeId);

            probadorRepository.save(probador);
        }
    }
}