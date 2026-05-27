package com.example.CodeMan.service;

import com.example.CodeMan.dto.UsuarioForm;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UsuarioService {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).+$");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario actual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return null;
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Transactional
    public void crear(UsuarioForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        validarPasswordObligatoria(form.getPassword());

        Usuario u = new Usuario();
        u.setName(form.getName().trim());
        u.setEmail(email);
        u.setRole(form.getRole());
        u.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        usuarioRepository.save(u);
    }

    @Transactional
    public void actualizar(Long id, UsuarioForm form) {
        Usuario u = buscar(id);
        String email = form.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("El correo ya está registrado por otro usuario.");
        }
        u.setName(form.getName().trim());
        u.setEmail(email);
        u.setRole(form.getRole());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            validarPasswordOpcional(form.getPassword());
            u.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }
        usuarioRepository.save(u);
    }

    @Transactional
    public void eliminar(Long id, Long usuarioActualId) {
        if (usuarioActualId != null && usuarioActualId.equals(id)) {
            throw new IllegalArgumentException("No puede eliminar su propia cuenta.");
        }
        Usuario u = buscar(id);
        usuarioRepository.delete(u);
    }

    private void validarPasswordObligatoria(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        validarPasswordOpcional(password);
    }

    private void validarPasswordOpcional(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener mínimo 8 caracteres.");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("La contraseña debe incluir letras y números.");
        }
    }
}
