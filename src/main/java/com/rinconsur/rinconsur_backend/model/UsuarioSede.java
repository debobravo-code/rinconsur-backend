package com.rinconsur.rinconsur_backend.model;

import jakarta.persistence.*;

/**
 * Sedes asignadas a cada usuario.
 * Un usuario puede tener varias sedes y una sede puede tener varios usuarios.
 * La pareja (usuario, sede) no se puede repetir.
 */
@Entity
@Table(
    name = "usuario_sede",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_usuario_sede",
            columnNames = {"usuario_id", "sede_id"}
        )
    }
)
public class UsuarioSede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "sede_id", nullable = false)
    private Integer sedeId;

    public UsuarioSede() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getSedeId() {
        return sedeId;
    }

    public void setSedeId(Integer sedeId) {
        this.sedeId = sedeId;
    }
}
