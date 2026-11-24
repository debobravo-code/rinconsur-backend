
package com.rinconsur.rinconsur_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FotoStorageService {

    private final Path directorio;

    public FotoStorageService(
            @Value("${app.upload.dir}") String uploadDir) {

        this.directorio = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    public String guardar(MultipartFile archivo) {

        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fotografia esta vacia");
        }

        if (archivo.getSize() > 10 * 1024 * 1024) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "La fotografia supera los 10 MB");
        }

        if (!"image/jpeg".equalsIgnoreCase(
                archivo.getContentType())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se permiten fotografias JPG");
        }

        try {
            Files.createDirectories(directorio);

            BufferedImage imagen;

            try (InputStream entrada =
                         archivo.getInputStream()) {

                imagen = ImageIO.read(entrada);
            }

            if (imagen == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El archivo no es una imagen valida");
            }

            String nombreArchivo =
                    UUID.randomUUID() + ".jpg";

            Path destino = directorio.resolve(nombreArchivo);

            if (!ImageIO.write(imagen, "jpg",
                    destino.toFile())) {

                throw new IOException(
                        "No se pudo generar la imagen JPG");
            }

            return nombreArchivo;

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo guardar la fotografia",
                    e);
        }
    }

    public Resource cargar(String nombreArchivo) {

        Path archivo = resolverRuta(nombreArchivo);

        try {
            Resource recurso =
                    new UrlResource(archivo.toUri());

            if (!Files.isRegularFile(archivo)
                    || !recurso.isReadable()) {

                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fotografia no encontrada");
            }

            return recurso;

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo cargar la fotografia",
                    e);
        }
    }

    public void eliminar(String nombreArchivo) {

        Path archivo = resolverRuta(nombreArchivo);

        try {
            Files.deleteIfExists(archivo);

        } catch (IOException e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo eliminar la fotografia",
                    e);
        }
    }

    private Path resolverRuta(String nombreArchivo) {

        if (nombreArchivo == null
                || !nombreArchivo.matches(
                        "[a-f0-9-]{36}\\.jpg")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre de fotografia invalido");
        }

        Path archivo = directorio
                .resolve(nombreArchivo)
                .normalize();

        if (!archivo.startsWith(directorio)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ruta de fotografia invalida");
        }

        return archivo;
    }
}
