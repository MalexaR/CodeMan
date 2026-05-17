package com.example.CodeMan.controller;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Patron;
import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.PatronService;
import com.example.CodeMan.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        if (!model.containsAttribute("patron")) {
            model.addAttribute("patron", new Patron());
        }
        addEnums(model);
        return "admin/patrones";
    }

    @GetMapping("/{id}/editar")
    public String editar(Authentication authentication, @PathVariable Long id, Model model) {
        requireAdmin(authentication);
        model.addAttribute("patron", patronService.buscar(id));
        model.addAttribute("patrones", patronService.listar());
        addEnums(model);
        return "admin/patrones";
    }

    @PostMapping
    public String guardar(Authentication authentication,
                          @Valid @ModelAttribute("patron") Patron patron,
                          BindingResult binding,
                          Model model,
                          RedirectAttributes flash) {
        requireAdmin(authentication);
        if (binding.hasErrors()) {
            model.addAttribute("patrones", patronService.listar());
            addEnums(model);
            return "admin/patrones";
        }
        if (patron.getNombre() != null && patronService.posibleDuplicado(patron.getNombre(), null)) {
            binding.rejectValue("nombre", "duplicate", "Ya existe un patrón con ese nombre.");
            model.addAttribute("patrones", patronService.listar());
            addEnums(model);
            return "admin/patrones";
        }
        patronService.guardar(patron);
        flash.addFlashAttribute("exito", "Patrón creado correctamente.");
        return "redirect:/admin/patrones";
    }

    @PostMapping("/{id}")
    public String actualizar(Authentication authentication,
                             @PathVariable Long id,
                             @Valid @ModelAttribute("patron") Patron form,
                             BindingResult binding,
                             Model model,
                             RedirectAttributes flash) {
        requireAdmin(authentication);
        if (binding.hasErrors()) {
            model.addAttribute("patrones", patronService.listar());
            addEnums(model);
            return "admin/patrones";
        }
        if (form.getNombre() != null && patronService.posibleDuplicado(form.getNombre(), id)) {
            binding.rejectValue("nombre", "duplicate", "Ya existe un patrón con ese nombre.");
            model.addAttribute("patrones", patronService.listar());
            addEnums(model);
            return "admin/patrones";
        }
        Patron existing = patronService.buscar(id);
        existing.setNombre(form.getNombre());
        existing.setCategoria(form.getCategoria());
        existing.setDescripcion(form.getDescripcion());
        existing.setProposito(form.getProposito());
        existing.setProblemaResuelto(form.getProblemaResuelto());
        existing.setCuandoUsar(form.getCuandoUsar());
        existing.setEstructura(form.getEstructura());
        existing.setDiagramaUmlUrl(form.getDiagramaUmlUrl());
        existing.setCodigoEjemplo(form.getCodigoEjemplo());
        existing.setReferencia(form.getReferencia());
        patronService.guardar(existing);
        flash.addFlashAttribute("exito", "Patrón actualizado correctamente.");
        return "redirect:/admin/patrones";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(Authentication authentication,
                           @PathVariable Long id,
                           RedirectAttributes flash) {
        requireAdmin(authentication);
        String advertencia = patronService.advertenciaEliminar(id);
        if (advertencia != null) {
            flash.addFlashAttribute("advertencia", advertencia);
            return "redirect:/admin/patrones";
        }
        patronService.eliminar(id);
        flash.addFlashAttribute("exito", "Patrón eliminado correctamente.");
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
