package com.example.CodeMan.controller;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {
    private final UsuarioService usuarioService;

    public GameController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/jugar")
    public String jugar() {
        return "game/select";
    }

    @GetMapping("/jugar/{categoria}")
    public String laberinto(@PathVariable Categoria categoria, Authentication authentication, Model model) {
        Usuario usuario = usuarioService.actual(authentication);
        model.addAttribute("categoria", categoria);
        model.addAttribute("usuario", usuario);
        return "game/maze";
    }
}
