package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.exception.ConflictException;
import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;
import com.rinconsur.rinconsur_backend.model.Sede;
import com.rinconsur.rinconsur_backend.repository.SedeRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeRepository sedeRepository;

    public SedeController(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
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

    // POST: registrar una nueva sede
    @PostMapping
    public ResponseEntity<Sede> crearSede(
            @Valid @RequestBody Sede sede) {

        if (sedeRepository.existsByNombre(sede.getNombre())) {
            throw new ConflictException("Ya existe una sede con ese nombre");
        }

        sede.setId(null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sedeRepository.save(sede));
    }

    // PUT: actualizar una sede existente
    @PutMapping("/{id}")
    public ResponseEntity<Sede> actualizarSede(
            @PathVariable Integer id,
            @Valid @RequestBody Sede datos) {

        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Sede no encontrada"));

        if (!sede.getNombre().equals(datos.getNombre())
                && sedeRepository.existsByNombre(datos.getNombre())) {
            throw new ConflictException("Ya existe una sede con ese nombre");
        }

        sede.setNombre(datos.getNombre());
        sede.setTiempoAlerta(datos.getTiempoAlerta());

        return ResponseEntity.ok(sedeRepository.save(sede));
    }

    // DELETE: eliminar una sede
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSede(@PathVariable Integer id) {

        if (!sedeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sede no encontrada");
        }

        sedeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}