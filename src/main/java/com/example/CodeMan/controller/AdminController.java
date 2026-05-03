package com.example.CodeMan.controller;

import com.example.CodeMan.model.*;
import com.example.CodeMan.repository.PreguntaRepository;
import com.example.CodeMan.repository.UsuarioRepository;
import com.example.CodeMan.service.PatronService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UsuarioRepository usuarioRepository;
    private final PatronService patronService;
    private final PreguntaRepository preguntaRepository;

    public AdminController(UsuarioRepository usuarioRepository, PatronService patronService,
            PreguntaRepository preguntaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.patronService = patronService;
        this.preguntaRepository = preguntaRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalPatrones", patronService.total());
        model.addAttribute("totalPreguntas", preguntaRepository.count());
        return "admin/index";
    }

    @GetMapping("/patrones")
    public String patrones(Model model) {
        model.addAttribute("patrones", patronService.listar());
        model.addAttribute("patron", new Patron());
        model.addAttribute("categorias",
                new Categoria[] { Categoria.CREACIONAL, Categoria.ESTRUCTURAL, Categoria.COMPORTAMIENTO });
        return "admin/patrones";
    }

    @PostMapping("/patrones")
    public String guardarPatron(@ModelAttribute Patron patron) {
        patronService.guardar(patron);
        return "redirect:/admin/patrones";
    }

    @GetMapping("/preguntas")
    public String preguntas(Model model) {
        model.addAttribute("preguntas", preguntaRepository.findAll());
        model.addAttribute("pregunta", new Pregunta());
        model.addAttribute("categorias",
                new Categoria[] { Categoria.CREACIONAL, Categoria.ESTRUCTURAL, Categoria.COMPORTAMIENTO });
        model.addAttribute("dificultades", Dificultad.values());
        model.addAttribute("opciones",
                new OpcionRespuesta[] { OpcionRespuesta.A, OpcionRespuesta.B, OpcionRespuesta.C, OpcionRespuesta.D });
        return "admin/preguntas";
    }

    @PostMapping("/preguntas")
    public String guardarPregunta(@ModelAttribute Pregunta pregunta) {
        preguntaRepository.save(pregunta);
        return "redirect:/admin/preguntas";
    }
}
