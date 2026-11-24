
package com.rinconsur.rinconsur_backend.repository;

import com.rinconsur.rinconsur_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByTipoUsuarioId(Integer tipoUsuarioId);

    boolean existsByTipoUsuarioId(Integer tipoUsuarioId);
}
