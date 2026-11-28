package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.dto.RegistroEdicionRequest;
import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;
import com.rinconsur.rinconsur_backend.model.RegistroOcupacion;
import com.rinconsur.rinconsur_backend.repository.RegistroOcupacionRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registros")
public class RegistroOcupacionController {

    private final RegistroOcupacionRepository registroRepository;

    public RegistroOcupacionController(
            RegistroOcupacionRepository registroRepository) {
        this.registroRepository = registroRepository;
    }

    @GetMapping
    public List<RegistroOcupacion> listar() {
        return registroRepository
                .findAllByOrderByFinMillisDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroOcupacion> buscarPorId(
            @PathVariable Integer id) {

        RegistroOcupacion registro = registroRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Registro no encontrado"));

        return ResponseEntity.ok(registro);
    }

    @GetMapping("/sede/{sedeId}")
    public List<RegistroOcupacion> listarPorSede(
            @PathVariable Integer sedeId) {

        return registroRepository
                .findBySedeIdOrderByFinMillisDesc(sedeId);
    }

    @GetMapping("/sede/{sedeId}/probador/{numero}")
    public List<RegistroOcupacion> listarPorProbador(
            @PathVariable Integer sedeId,
            @PathVariable Integer numero) {

        return registroRepository
                .findBySedeIdAndNumeroProbadorOrderByFinMillisDesc(
                        sedeId, numero);
    }

    @GetMapping("/sede/{sedeId}/prenda-olvidada")
    public List<RegistroOcupacion> listarPorPrendaOlvidada(
            @PathVariable Integer sedeId,
            @RequestParam boolean valor) {

        return registroRepository
                .findBySedeIdAndPrendaOlvidadaOrderByFinMillisDesc(
                        sedeId, valor);
    }

    @GetMapping("/sede/{sedeId}/fechas")
    public ResponseEntity<List<RegistroOcupacion>> listarPorFechas(
            @PathVariable Integer sedeId,
            @RequestParam Long desde,
            @RequestParam Long hasta) {

        if (desde > hasta || desde < 0) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                registroRepository
                    .findBySedeIdAndFinMillisBetweenOrderByFinMillisDesc(
                            sedeId, desde, hasta));
    }

    // PUT: corregir cantidad de prendas, prenda olvidada y observacion.
    // La sede, el probador y los horarios no se pueden cambiar.
    @PutMapping("/{id}")
    public ResponseEntity<RegistroOcupacion> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody RegistroEdicionRequest request) {

        RegistroOcupacion registro = registroRepository.findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException("Registro no encontrado"));

        registro.setCantidadPrendas(request.cantidadPrendas());
        registro.setPrendaOlvidada(request.prendaOlvidada());
        registro.setObservacion(
                request.observacion() == null
                        ? "" : request.observacion().trim());

        return ResponseEntity.ok(registroRepository.save(registro));
    }
}
