package com.example.CodeMan.controller;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Patron;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.PatronService;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TeoriaController {
    private final PatronService patronService;
    private final UsuarioService usuarioService;

    public TeoriaController(PatronService patronService, UsuarioService usuarioService) {
        this.patronService = patronService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/teoria")
    public String teoria(Model model) {
        model.addAttribute("creacionales", patronService.porCategoria(Categoria.CREACIONAL));
        model.addAttribute("estructurales", patronService.porCategoria(Categoria.ESTRUCTURAL));
        model.addAttribute("comportamiento", patronService.porCategoria(Categoria.COMPORTAMIENTO));
        return "teoria/index";
    }

    @GetMapping("/teoria/{id}")
    public String detalle(@PathVariable Long id, Authentication authentication, Model model) {
        Usuario usuario = usuarioService.actual(authentication);
        Patron patron = patronService.buscar(id);
        model.addAttribute("patron", patron);
        model.addAttribute("revisado", patronService.estaRevisado(usuario, patron));
        return "teoria/detalle";
    }

    @PostMapping("/teoria/{id}/revisar")
    public String revisar(@PathVariable Long id, Authentication authentication) {
        patronService.marcarRevisado(usuarioService.actual(authentication), id);
        return "redirect:/teoria/" + id;
    }
}
