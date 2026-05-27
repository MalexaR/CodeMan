package com.example.CodeMan.controller;

import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.UsuarioRepository;
import com.example.CodeMan.service.PatronService;
import com.example.CodeMan.service.PreguntaService;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final PatronService patronService;
    private final PreguntaService preguntaService;
    private final UsuarioService usuarioService;

    public AdminController(UsuarioRepository usuarioRepository,
            PatronService patronService,
            PreguntaService preguntaService,
            UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.patronService = patronService;
        this.preguntaService = preguntaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String index(Authentication authentication, Model model) {
        Usuario actual = requireAdmin(authentication);
        model.addAttribute("usuario", actual);
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalPatrones", patronService.total());
        model.addAttribute("totalPreguntas", preguntaService.total());
        return "admin/index";
    }

    private Usuario requireAdmin(Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        if (usuario == null || usuario.getRole() != Rol.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso restringido a administradores");
        }
        return usuario;
    }
}
