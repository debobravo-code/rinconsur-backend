package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.exception.ConflictException;
import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;
import com.rinconsur.rinconsur_backend.model.Sede;
import com.rinconsur.rinconsur_backend.model.User;
import com.rinconsur.rinconsur_backend.model.UsuarioSede;
import com.rinconsur.rinconsur_backend.repository.SedeRepository;
import com.rinconsur.rinconsur_backend.repository.UserRepository;
import com.rinconsur.rinconsur_backend.repository.TipoUsuarioRepository;
import com.rinconsur.rinconsur_backend.repository.UsuarioSedeRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final SedeRepository sedeRepository;
    private final UsuarioSedeRepository usuarioSedeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(
            UserRepository repository,
            TipoUsuarioRepository tipoUsuarioRepository,
            SedeRepository sedeRepository,
            UsuarioSedeRepository usuarioSedeRepository) {

        this.repository = repository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.sedeRepository = sedeRepository;
        this.usuarioSedeRepository = usuarioSedeRepository;
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

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

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

        // Si no se envia contrasena, se conserva la actual
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            existente.setPassword(
                    passwordEncoder.encode(datos.getPassword())
            );
        }

        return ResponseEntity.ok(repository.save(existente));
    }

    // Eliminar usuario (tambien elimina sus sedes asignadas)
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        usuarioSedeRepository.deleteByUsuarioId(id);
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // Sedes asignadas a un usuario
    @GetMapping("/{id}/sedes")
    public List<Sede> listarSedes(@PathVariable Integer id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        List<Integer> sedesIds = usuarioSedeRepository
                .findByUsuarioId(id)
                .stream()
                .map(UsuarioSede::getSedeId)
                .toList();

        return sedeRepository.findAllById(sedesIds);
    }

    // Reemplaza las sedes asignadas a un usuario
    @Transactional
    @PutMapping("/{id}/sedes")
    public ResponseEntity<List<Sede>> asignarSedes(
            @PathVariable Integer id,
            @RequestBody List<Integer> sedesIds) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        List<Integer> ids = sedesIds.stream().distinct().toList();

        List<Sede> sedes = sedeRepository.findAllById(ids);

        // Alguna sede indicada no existe
        if (sedes.size() != ids.size()) {
            return ResponseEntity.badRequest().build();
        }

        usuarioSedeRepository.deleteByUsuarioId(id);

        // flush: ejecuta el DELETE antes de los INSERT
        usuarioSedeRepository.flush();

        for (Integer sedeId : ids) {

            UsuarioSede asignacion = new UsuarioSede();
            asignacion.setUsuarioId(id);
            asignacion.setSedeId(sedeId);

            usuarioSedeRepository.save(asignacion);
        }

        return ResponseEntity.ok(sedes);
    }
}