
package com.rinconsur.rinconsur_backend.controller;

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
        return sedeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: registrar una nueva sede
    @PostMapping
    public ResponseEntity<?> crearSede(@Valid @RequestBody Sede sede) {

        if (sedeRepository.existsByNombre(sede.getNombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe una sede con ese nombre");
        }

        sede.setId(null);
        Sede guardada = sedeRepository.save(sede);

        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // PUT: actualizar una sede existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSede(
            @PathVariable Integer id,
            @Valid @RequestBody Sede datos) {

        return sedeRepository.findById(id).map(sede -> {

            if (!sede.getNombre().equals(datos.getNombre())
                    && sedeRepository.existsByNombre(datos.getNombre())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body((Object) "Ya existe una sede con ese nombre");
            }

            sede.setNombre(datos.getNombre());
            sede.setTiempoAlerta(datos.getTiempoAlerta());

            return ResponseEntity.ok(
                    (Object) sedeRepository.save(sede)
            );

        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE: eliminar una sede
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSede(@PathVariable Integer id) {

        if (!sedeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        sedeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
