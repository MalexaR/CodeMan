package com.example.CodeMan.controller;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Dificultad;
import com.example.CodeMan.model.OpcionRespuesta;
import com.example.CodeMan.model.Pregunta;
import com.example.CodeMan.model.Rol;
import com.example.CodeMan.model.TipoEvento;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.PreguntaService;
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
@RequestMapping("/admin/preguntas")
public class AdminPreguntaController {

    private final PreguntaService preguntaService;
    private final UsuarioService usuarioService;

    public AdminPreguntaController(PreguntaService preguntaService, UsuarioService usuarioService) {
        this.preguntaService = preguntaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        requireAdmin(authentication);
        model.addAttribute("preguntas", preguntaService.listar());
        model.addAttribute("pregunta", new Pregunta());
        addEnums(model);
        return "admin/preguntas";
    }

    @GetMapping("/nueva")
    public String nueva(Authentication authentication, Model model) {
        requireAdmin(authentication);
        model.addAttribute("pregunta", new Pregunta());
        model.addAttribute("preguntas", preguntaService.listar());
        addEnums(model);
        return "admin/preguntas";
    }

    @PostMapping
    public String guardar(Authentication authentication, @ModelAttribute Pregunta pregunta) {
        requireAdmin(authentication);
        preguntaService.guardar(pregunta);
        return "redirect:/admin/preguntas";
    }

    @GetMapping("/{id}/editar")
    public String editar(Authentication authentication, @PathVariable Long id, Model model) {
        requireAdmin(authentication);
        model.addAttribute("pregunta", preguntaService.buscar(id));
        model.addAttribute("preguntas", preguntaService.listar());
        addEnums(model);
        return "admin/preguntas";
    }

    @PostMapping("/{id}")
    public String actualizar(Authentication authentication, @PathVariable Long id,
            @ModelAttribute Pregunta pregunta) {
        requireAdmin(authentication);
        pregunta.setId(id);
        preguntaService.guardar(pregunta);
        return "redirect:/admin/preguntas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(Authentication authentication, @PathVariable Long id) {
        requireAdmin(authentication);
        preguntaService.eliminar(id);
        return "redirect:/admin/preguntas";
    }

    private void addEnums(Model model) {
        model.addAttribute("categorias",
                new Categoria[]{ Categoria.CREACIONAL, Categoria.ESTRUCTURAL, Categoria.COMPORTAMIENTO });
        model.addAttribute("dificultades", Dificultad.values());
        model.addAttribute("opciones", OpcionRespuesta.values());
        model.addAttribute("tiposEvento", TipoEvento.values());
    }

    private void requireAdmin(Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        if (usuario == null || usuario.getRole() != Rol.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso restringido a administradores");
        }
    }
}
