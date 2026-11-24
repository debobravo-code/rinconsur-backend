
package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.service.FotoStorageService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/fotos")
public class FotoController {

    private final FotoStorageService fotoStorageService;

    public FotoController(
            FotoStorageService fotoStorageService) {

        this.fotoStorageService = fotoStorageService;
    }

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, String>> subirFoto(
            @RequestParam("archivo") MultipartFile archivo) {

        String nombreArchivo =
                fotoStorageService.guardar(archivo);

        String fotoUri = "/api/fotos/" + nombreArchivo;

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("fotoUri", fotoUri));
    }

    @GetMapping("/{nombreArchivo}")
    public ResponseEntity<Resource> obtenerFoto(
            @PathVariable String nombreArchivo) {

        Resource recurso =
                fotoStorageService.cargar(nombreArchivo);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(recurso);
    }
}
