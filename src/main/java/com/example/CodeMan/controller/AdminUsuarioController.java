package com.example.CodeMan.controller;

import com.example.CodeMan.dto.UsuarioForm;
import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.Usuario;
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
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("form")
    public UsuarioForm defaultForm() {
        return new UsuarioForm();
    }

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        Usuario actual = requireAdmin(authentication);
        model.addAttribute("usuario", actual);
        model.addAttribute("usuarios", usuarioService.listar());
        addEnums(model);
        return "admin/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(Authentication authentication, @PathVariable Long id, Model model) {
        Usuario actual = requireAdmin(authentication);
        Usuario u = usuarioService.buscar(id);

        UsuarioForm form = new UsuarioForm();
        form.setId(u.getId());
        form.setName(u.getName());
        form.setEmail(u.getEmail());
        form.setRole(u.getRole());

        model.addAttribute("usuario", actual);
        model.addAttribute("form", form);
        model.addAttribute("usuarios", usuarioService.listar());
        addEnums(model);
        return "admin/usuarios";
    }

    @PostMapping
    public String crear(Authentication authentication,
                        @Valid @ModelAttribute("form") UsuarioForm form,
                        BindingResult binding,
                        Model model,
                        RedirectAttributes flash) {
        Usuario actual = requireAdmin(authentication);
        if (!binding.hasErrors()) {
            try {
                usuarioService.crear(form);
                flash.addFlashAttribute("exito", "Usuario creado correctamente.");
                return "redirect:/admin/usuarios";
            } catch (IllegalArgumentException ex) {
                model.addAttribute("serviceError", ex.getMessage());
            }
        }
        model.addAttribute("usuario", actual);
        model.addAttribute("usuarios", usuarioService.listar());
        addEnums(model);
        return "admin/usuarios";
    }

    @PostMapping("/{id}")
    public String actualizar(Authentication authentication,
                             @PathVariable Long id,
                             @Valid @ModelAttribute("form") UsuarioForm form,
                             BindingResult binding,
                             Model model,
                             RedirectAttributes flash) {
        Usuario actual = requireAdmin(authentication);
        form.setId(id);
        if (!binding.hasErrors()) {
            try {
                usuarioService.actualizar(id, form);
                flash.addFlashAttribute("exito", "Usuario actualizado correctamente.");
                return "redirect:/admin/usuarios";
            } catch (IllegalArgumentException ex) {
                model.addAttribute("serviceError", ex.getMessage());
            }
        }
        model.addAttribute("usuario", actual);
        model.addAttribute("usuarios", usuarioService.listar());
        addEnums(model);
        return "admin/usuarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(Authentication authentication,
                           @PathVariable Long id,
                           RedirectAttributes flash) {
        Usuario actual = requireAdmin(authentication);
        try {
            usuarioService.eliminar(id, actual.getId());
            flash.addFlashAttribute("exito", "Usuario eliminado correctamente.");
        } catch (IllegalArgumentException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            flash.addFlashAttribute("error",
                    "No se puede eliminar el usuario por restricciones de integridad referencial.");
        }
        return "redirect:/admin/usuarios";
    }

    private void addEnums(Model model) {
        model.addAttribute("roles", Rol.values());
    }

    private Usuario requireAdmin(Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        if (usuario == null || usuario.getRole() != Rol.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso restringido a administradores");
        }
        return usuario;
    }
}
