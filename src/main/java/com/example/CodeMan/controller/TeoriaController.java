package com.example.CodeMan.controller;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Patron;
import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.PatronService;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class TeoriaController {

    private final PatronService patronService;
    private final UsuarioService usuarioService;

    public TeoriaController(PatronService patronService, UsuarioService usuarioService) {
        this.patronService = patronService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/teoria")
    public String teoria(Authentication authentication, Model model) {
        if (esAdmin(authentication)) return "redirect:/admin";
        model.addAttribute("creacionales", patronService.porCategoria(Categoria.CREACIONAL));
        model.addAttribute("estructurales", patronService.porCategoria(Categoria.ESTRUCTURAL));
        model.addAttribute("comportamiento", patronService.porCategoria(Categoria.COMPORTAMIENTO));
        return "teoria/index";
    }

    @GetMapping("/teoria/{id}")
    public String detalle(@PathVariable Long id, Authentication authentication, Model model) {
        if (esAdmin(authentication)) return "redirect:/admin";
        Usuario usuario = usuarioService.actual(authentication);
        Patron patron = patronService.buscar(id);
        model.addAttribute("patron", patron);
        model.addAttribute("revisado", patronService.estaRevisado(usuario, patron));
        model.addAttribute("patronAnterior", patronService.anterior(patron).orElse(null));
        model.addAttribute("patronSiguiente", patronService.siguiente(patron).orElse(null));
        return "teoria/detalle";
    }

    @PostMapping("/teoria/{id}/revisar")
    public String revisar(@PathVariable Long id, Authentication authentication) {
        if (esAdmin(authentication)) return "redirect:/admin";
        patronService.marcarRevisado(usuarioService.actual(authentication), id);
        return "redirect:/teoria/" + id;
    }

    private boolean esAdmin(Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        return usuario != null && usuario.getRole() == Rol.ADMIN;
    }

    @GetMapping("/api/teoria/patrones")
    @ResponseBody
    public List<Patron> listarJson(@RequestParam(required = false) Categoria categoria) {
        return categoria != null ? patronService.porCategoria(categoria) : patronService.listar();
    }

    @GetMapping("/api/teoria/patrones/{id}")
    @ResponseBody
    public Patron detalleJson(@PathVariable Long id) {
        return patronService.buscar(id);
    }

    @GetMapping("/api/teoria/patrones/buscar")
    @ResponseBody
    public List<Patron> buscar(@RequestParam String q) {
        return patronService.listar().stream()
                .filter(p -> p.getNombre().toLowerCase().contains(q.toLowerCase())
                        || (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(q.toLowerCase())))
                .toList();
    }
}
