package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.exception.ConflictException;
import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;
import com.rinconsur.rinconsur_backend.model.TipoUsuario;
import com.rinconsur.rinconsur_backend.repository.TipoUsuarioRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-usuario")
public class TipoUsuarioController {

    private final TipoUsuarioRepository repository;

    public TipoUsuarioController(TipoUsuarioRepository repository) {
        this.repository = repository;
    }

    // Listar todos los tipos de usuario
    @GetMapping
    public List<TipoUsuario> listar() {
        return repository.findAll();
    }

    // Buscar tipo de usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<TipoUsuario> buscarPorId(@PathVariable Integer id) {

        TipoUsuario tipoUsuario = repository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Tipo de usuario no encontrado"));

        return ResponseEntity.ok(tipoUsuario);
    }

    // Crear tipo de usuario
    @PostMapping
    public ResponseEntity<TipoUsuario> crear(
            @Valid @RequestBody TipoUsuario tipoUsuario) {

        if (repository.existsByTipoUsuario(tipoUsuario.getTipoUsuario())) {
            throw new ConflictException("El tipo de usuario ya existe");
        }

        tipoUsuario.setId(null);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(tipoUsuario));
    }

    // Actualizar tipo de usuario
    @PutMapping("/{id}")
    public ResponseEntity<TipoUsuario> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody TipoUsuario datos) {

        TipoUsuario existente = repository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Tipo de usuario no encontrado"));

        if (id.equals(TipoUsuario.ID_ADMINISTRADOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        TipoUsuario duplicado = repository
                .findByTipoUsuario(datos.getTipoUsuario())
                .orElse(null);

        if (duplicado != null && !duplicado.getId().equals(id)) {
            throw new ConflictException("El tipo de usuario ya existe");
        }

        existente.setTipoUsuario(datos.getTipoUsuario());

        return ResponseEntity.ok(repository.save(existente));
    }

    // Eliminar tipo de usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de usuario no encontrado");
        }

        if (id.equals(TipoUsuario.ID_ADMINISTRADOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}