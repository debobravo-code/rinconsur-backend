package com.rinconsur.rinconsur_backend.service;

import com.rinconsur.rinconsur_backend.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FotoReferenciaService {

    private static final String PREFIJO = "/api/fotos/";

    private final FotoStorageService fotoStorageService;

    public FotoReferenciaService(
            FotoStorageService fotoStorageService) {

        this.fotoStorageService = fotoStorageService;
    }

    public String validar(String fotoUri) {

        if (fotoUri == null || fotoUri.isBlank()) {
            return null;
        }

        if (!fotoUri.startsWith(PREFIJO)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fotografia debe estar almacenada en el servidor");
        }

        String nombreArchivo =
                fotoUri.substring(PREFIJO.length());

        try {

            fotoStorageService.cargar(nombreArchivo);

        } catch (ResourceNotFoundException e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fotografia indicada no existe");
        }

        return PREFIJO + nombreArchivo;
    }
}