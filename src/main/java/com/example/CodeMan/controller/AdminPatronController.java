package com.example.CodeMan.controller;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Patron;
import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.PatronService;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/admin/patrones")
public class AdminPatronController {

    private final PatronService patronService;
    private final UsuarioService usuarioService;

    public AdminPatronController(PatronService patronService, UsuarioService usuarioService) {
        this.patronService = patronService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        requireAdmin(authentication);
        model.addAttribute("patrones", patronService.listar());
        model.addAttribute("patron", new Patron());
        addEnums(model);
        return "admin/patrones";
    }

    @GetMapping("/nuevo")
    public String nuevo(Authentication authentication, Model model) {
        requireAdmin(authentication);
        model.addAttribute("patron", new Patron());
        model.addAttribute("patrones", patronService.listar());
        addEnums(model);
        return "admin/patrones";
    }

    @PostMapping
    public String guardar(Authentication authentication, @ModelAttribute Patron patron) {
        requireAdmin(authentication);
        patronService.guardar(patron);
        return "redirect:/admin/patrones";
    }

    @GetMapping("/{id}/editar")
    public String editar(Authentication authentication, @PathVariable Long id, Model model) {
        requireAdmin(authentication);
        model.addAttribute("patron", patronService.buscar(id));
        model.addAttribute("patrones", patronService.listar());
        addEnums(model);
        return "admin/patrones";
    }

    @PostMapping("/{id}")
    public String actualizar(Authentication authentication, @PathVariable Long id,
            @ModelAttribute Patron patron) {
        requireAdmin(authentication);
        patron.setId(id);
        patronService.guardar(patron);
        return "redirect:/admin/patrones";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(Authentication authentication, @PathVariable Long id) {
        requireAdmin(authentication);
        patronService.eliminar(id);
        return "redirect:/admin/patrones";
    }

    private void addEnums(Model model) {
        model.addAttribute("categorias",
                new Categoria[]{ Categoria.CREACIONAL, Categoria.ESTRUCTURAL, Categoria.COMPORTAMIENTO });
    }

    private void requireAdmin(Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        if (usuario == null || usuario.getRole() != Rol.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso restringido a administradores");
        }
    }
}
