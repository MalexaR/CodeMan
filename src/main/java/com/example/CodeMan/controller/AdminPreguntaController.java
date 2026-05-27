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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Usuario actual = requireAdmin(authentication);
        model.addAttribute("usuario", actual);
        model.addAttribute("preguntas", preguntaService.listar());
        if (!model.containsAttribute("pregunta")) {
            model.addAttribute("pregunta", new Pregunta());
        }
        addEnums(model);
        return "admin/preguntas";
    }

    @GetMapping("/{id}/editar")
    public String editar(Authentication authentication, @PathVariable Long id, Model model) {
        Usuario actual = requireAdmin(authentication);
        model.addAttribute("usuario", actual);
        model.addAttribute("pregunta", preguntaService.buscar(id));
        model.addAttribute("preguntas", preguntaService.listar());
        addEnums(model);
        return "admin/preguntas";
    }

    @PostMapping
    public String guardar(Authentication authentication,
                          @Valid @ModelAttribute("pregunta") Pregunta pregunta,
                          BindingResult binding,
                          @RequestParam(name = "confirmar", defaultValue = "false") boolean confirmar,
                          Model model,
                          RedirectAttributes flash) {
        Usuario actual = requireAdmin(authentication);
        if (binding.hasErrors()) {
            model.addAttribute("usuario", actual);
            model.addAttribute("preguntas", preguntaService.listar());
            addEnums(model);
            return "admin/preguntas";
        }
        if (!confirmar && pregunta.getEnunciado() != null
                && preguntaService.posibleDuplicado(pregunta.getEnunciado(), null)) {
            model.addAttribute("usuario", actual);
            model.addAttribute("warnDuplicado", true);
            model.addAttribute("preguntas", preguntaService.listar());
            addEnums(model);
            return "admin/preguntas";
        }
        preguntaService.guardar(pregunta);
        flash.addFlashAttribute("exito", "Pregunta creada correctamente.");
        return "redirect:/admin/preguntas";
    }

    @PostMapping("/{id}")
    public String actualizar(Authentication authentication,
                             @PathVariable Long id,
                             @Valid @ModelAttribute("pregunta") Pregunta form,
                             BindingResult binding,
                             @RequestParam(name = "confirmar", defaultValue = "false") boolean confirmar,
                             Model model,
                             RedirectAttributes flash) {
        Usuario actual = requireAdmin(authentication);
        if (binding.hasErrors()) {
            model.addAttribute("usuario", actual);
            model.addAttribute("preguntas", preguntaService.listar());
            addEnums(model);
            return "admin/preguntas";
        }
        if (!confirmar && form.getEnunciado() != null
                && preguntaService.posibleDuplicado(form.getEnunciado(), id)) {
            model.addAttribute("usuario", actual);
            model.addAttribute("warnDuplicado", true);
            model.addAttribute("preguntas", preguntaService.listar());
            addEnums(model);
            return "admin/preguntas";
        }
        Pregunta existing = preguntaService.buscar(id);
        existing.setEnunciado(form.getEnunciado());
        existing.setOpcionA(form.getOpcionA());
        existing.setOpcionB(form.getOpcionB());
        existing.setOpcionC(form.getOpcionC());
        existing.setOpcionD(form.getOpcionD());
        existing.setRespuestaCorrecta(form.getRespuestaCorrecta());
        existing.setExplicacion(form.getExplicacion());
        existing.setCategoria(form.getCategoria());
        existing.setDificultad(form.getDificultad());
        existing.setTipoEvento(form.getTipoEvento());
        preguntaService.guardar(existing);
        flash.addFlashAttribute("exito", "Pregunta actualizada correctamente.");
        return "redirect:/admin/preguntas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(Authentication authentication,
                           @PathVariable Long id,
                           RedirectAttributes flash) {
        requireAdmin(authentication);
        if (preguntaService.tieneRespuestas(id)) {
            flash.addFlashAttribute("error",
                    "No se puede eliminar: esta pregunta tiene respuestas de estudiantes registradas que forman parte del historial de partidas.");
            return "redirect:/admin/preguntas";
        }
        preguntaService.eliminar(id);
        flash.addFlashAttribute("exito", "Pregunta eliminada correctamente.");
        return "redirect:/admin/preguntas";
    }

    private void addEnums(Model model) {
        if (!model.containsAttribute("warnDuplicado")) {
            model.addAttribute("warnDuplicado", false);
        }
        model.addAttribute("categorias",
                new Categoria[]{ Categoria.CREACIONAL, Categoria.ESTRUCTURAL, Categoria.COMPORTAMIENTO });
        model.addAttribute("dificultades", Dificultad.values());
        model.addAttribute("opciones", OpcionRespuesta.values());
        model.addAttribute("tiposEvento", TipoEvento.values());
    }

    private Usuario requireAdmin(Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        if (usuario == null || usuario.getRole() != Rol.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso restringido a administradores");
        }
        return usuario;
    }
}
