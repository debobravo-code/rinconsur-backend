
package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.model.User;
import com.rinconsur.rinconsur_backend.repository.UserRepository;
import com.rinconsur.rinconsur_backend.repository.TipoUsuarioRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository repository,
            TipoUsuarioRepository tipoUsuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Listar usuarios
    @GetMapping
    public List<User> listar() {
        return repository.findAll();
    }

    // Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> buscarPorId(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Registrar usuario
    @PostMapping
    public ResponseEntity<User> crear(@Valid @RequestBody User user) {

        if (repository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (!tipoUsuarioRepository.existsById(user.getTipoUsuarioId())) {
            return ResponseEntity.badRequest().build();
        }

        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(user));
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody User datos) {

        User existente = repository.findById(id).orElse(null);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        User duplicado = repository
                .findByEmail(datos.getEmail())
                .orElse(null);

        if (duplicado != null && !duplicado.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (!tipoUsuarioRepository.existsById(datos.getTipoUsuarioId())) {
            return ResponseEntity.badRequest().build();
        }

        existente.setName(datos.getName());
        existente.setEmail(datos.getEmail());
        existente.setTipoUsuarioId(datos.getTipoUsuarioId());
        existente.setPassword(
                passwordEncoder.encode(datos.getPassword())
        );

        return ResponseEntity.ok(repository.save(existente));
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
