package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.exception.ConflictException;
import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;
import com.rinconsur.rinconsur_backend.model.User;
import com.rinconsur.rinconsur_backend.repository.UserRepository;
import com.rinconsur.rinconsur_backend.repository.TipoUsuarioRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(
            UserRepository repository,
            TipoUsuarioRepository tipoUsuarioRepository) {

        this.repository = repository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        
    }

    // Listar usuarios
    @GetMapping
    public List<User> listar() {
        return repository.findAll();
    }

    // Buscar usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> buscarPorId(@PathVariable Integer id) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Usuario no encontrado"));

        return ResponseEntity.ok(user);
    }

    // Registrar usuario
    @PostMapping
    public ResponseEntity<User> crear(@Valid @RequestBody User user) {

        if (repository.existsByEmail(user.getEmail())) {
            throw new ConflictException("El correo ya existe");
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

        User existente = repository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Usuario no encontrado"));

        User duplicado = repository
                .findByEmail(datos.getEmail())
                .orElse(null);

        if (duplicado != null && !duplicado.getId().equals(id)) {
            throw new ConflictException("El correo ya existe");
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
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}