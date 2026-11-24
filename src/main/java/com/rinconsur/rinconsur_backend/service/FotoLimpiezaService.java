
package com.rinconsur.rinconsur_backend.service;

import com.rinconsur.rinconsur_backend.repository.ReportePrendaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FotoLimpiezaService {

    private static final String PREFIJO = "/api/fotos/";

    private static final Logger logger =
            LoggerFactory.getLogger(FotoLimpiezaService.class);

    private final ReportePrendaRepository reporteRepository;
    private final FotoStorageService fotoStorageService;

    public FotoLimpiezaService(
            ReportePrendaRepository reporteRepository,
            FotoStorageService fotoStorageService) {

        this.reporteRepository = reporteRepository;
        this.fotoStorageService = fotoStorageService;
    }

    public void limpiarDespuesDeCommit(String fotoUri) {

        if (fotoUri == null
                || fotoUri.isBlank()
                || !fotoUri.startsWith(PREFIJO)) {
            return;
        }

        if (!TransactionSynchronizationManager
                .isSynchronizationActive()) {

            throw new IllegalStateException(
                    "Se requiere una transaccion activa");
        }

        TransactionSynchronizationManager
                .registerSynchronization(
                    new TransactionSynchronization() {

                        @Override
                        public void afterCommit() {
                            limpiarSiNoTieneReferencias(fotoUri);
                        }
                    }
                );
    }

    private void limpiarSiNoTieneReferencias(String fotoUri) {

        try {

            if (reporteRepository.existsByFotoUri(fotoUri)) {
                return;
            }

            String nombreArchivo =
                    fotoUri.substring(PREFIJO.length());

            fotoStorageService.eliminar(nombreArchivo);

            logger.info(
                    "Fotografia eliminada: {}", nombreArchivo);

        } catch (Exception e) {

            logger.warn(
                    "No se pudo limpiar la fotografia: {}",
                    fotoUri, e);
        }
    }
}
