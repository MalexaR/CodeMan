package com.example.CodeMan.controller;

import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.ProgresoService;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UsuarioService usuarioService;
    private final ProgresoService progresoService;

    public HomeController(UsuarioService usuarioService, ProgresoService progresoService) {
        this.usuarioService = usuarioService;
        this.progresoService = progresoService;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioService.actual(authentication);
        if (usuario == null) {
            return "redirect:/login";
        }
        if (usuario.getRole() == Rol.ADMIN) return "redirect:/admin";
        model.addAttribute("usuario", usuario);
        model.addAttribute("progreso", progresoService.resumen(usuario));
        model.addAttribute("isAdmin", false);
        return "home";
    }
}
