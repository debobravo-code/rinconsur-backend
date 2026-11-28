package com.rinconsur.rinconsur_backend.repository;

import com.rinconsur.rinconsur_backend.model.UsuarioSede;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioSedeRepository
        extends JpaRepository<UsuarioSede, Integer> {

    List<UsuarioSede> findByUsuarioId(Integer usuarioId);

    // Los metodos deleteBy se llaman desde metodos @Transactional
    void deleteByUsuarioId(Integer usuarioId);

    void deleteBySedeId(Integer sedeId);
}
