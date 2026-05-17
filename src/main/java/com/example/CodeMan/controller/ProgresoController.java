package com.example.CodeMan.controller;

import com.example.CodeMan.model.Progreso;
import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.ProgresoService;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProgresoController {

    private final UsuarioService usuarioService;
    private final ProgresoService progresoService;

    public ProgresoController(UsuarioService usuarioService, ProgresoService progresoService) {
        this.usuarioService = usuarioService;
        this.progresoService = progresoService;
    }

    @GetMapping("/progreso")
    public String progreso(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.actual(authentication);
        if (usuario != null && usuario.getRole() == Rol.ADMIN) return "redirect:/admin";
        model.addAttribute("usuario", usuario);
        model.addAttribute("progreso", progresoService.resumen(usuario));
        return "progreso/index";
    }

    @GetMapping("/api/progreso/me")
    @ResponseBody
    public Progreso resumenJson(Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        return progresoService.resumen(usuario);
    }
}
