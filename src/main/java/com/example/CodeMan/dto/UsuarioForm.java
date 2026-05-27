package com.example.CodeMan.dto;

import com.example.CodeMan.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioForm {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 2, max = 255, message = "El nombre debe tener entre 2 y 255 caracteres.")
    private String name;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Digite un correo válido.")
    @Size(max = 320)
    private String email;

    @NotNull(message = "Seleccione un rol.")
    private Rol role = Rol.ESTUDIANTE;

    // En creación es obligatorio; en edición se deja en blanco para no cambiarlo.
    private String password;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Rol getRole() { return role; }
    public void setRole(Rol role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
