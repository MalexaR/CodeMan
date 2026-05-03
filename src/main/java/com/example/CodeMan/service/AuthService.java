package com.example.CodeMan.service;

import com.example.CodeMan.dto.RegisterForm;
import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Set<String> DOMINIOS_PERMITIDOS = Set.of("unillanos.edu.co", "unillanos.edu");

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registrar(RegisterForm form) {
        String email = form.getEmail().trim().toLowerCase();
        validarDominioInstitucional(email);
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setName(form.getName().trim());
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        usuario.setRole(Rol.ESTUDIANTE);
        usuarioRepository.save(usuario);
    }

    private void validarDominioInstitucional(String email) {
        int at = email.lastIndexOf('@');
        String dominio = at >= 0 ? email.substring(at + 1) : "";
        if (!DOMINIOS_PERMITIDOS.contains(dominio)) {
            throw new IllegalArgumentException("Se requiere un correo institucional válido de Unillanos.");
        }
    }
}
