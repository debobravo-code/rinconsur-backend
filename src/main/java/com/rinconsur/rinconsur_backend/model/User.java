
package com.rinconsur.rinconsur_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es valido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @NotNull(message = "El tipo de usuario es obligatorio")
    @Column(name = "tipoUsuarioId", nullable = false)
    private Integer tipoUsuarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "tipoUsuarioId",
        insertable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_user_tipo_usuario")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    private TipoUsuario tipoUsuario;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTipoUsuarioId() {
        return tipoUsuarioId;
    }

    public void setTipoUsuarioId(Integer tipoUsuarioId) {
        this.tipoUsuarioId = tipoUsuarioId;
    }
}
