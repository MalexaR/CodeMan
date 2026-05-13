package com.example.CodeMan.controller;

import com.example.CodeMan.model.Progreso;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.ProgresoService;
import com.example.CodeMan.service.ReporteService;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reporte")
public class ReporteController {

    private final ReporteService reporteService;
    private final ProgresoService progresoService;
    private final UsuarioService usuarioService;

    public ReporteController(ReporteService reporteService,
            ProgresoService progresoService,
            UsuarioService usuarioService) {
        this.reporteService = reporteService;
        this.progresoService = progresoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String index(Authentication authentication, Model model, RedirectAttributes flash) {
        Usuario usuario = usuarioService.actual(authentication);
        Progreso progreso = progresoService.resumen(usuario);

        if (!progreso.isDesafioFinalCompletado()) {
            flash.addFlashAttribute("error", "Debes completar el Desafío Final para ver tu reporte.");
            return "redirect:/progreso";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("datos", reporteService.construirDatos(usuario));
        return "reporte/index";
    }

    @GetMapping("/descargar")
    public ResponseEntity<byte[]> descargar(Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        Progreso progreso = progresoService.resumen(usuario);

        if (!progreso.isDesafioFinalCompletado()) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdf = reporteService.generarPdf(usuario);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte-codeman.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
