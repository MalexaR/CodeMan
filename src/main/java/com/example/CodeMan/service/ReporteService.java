package com.example.CodeMan.service;

import com.example.CodeMan.dto.ReporteDtos.DesempenoPorCategoria;
import com.example.CodeMan.dto.ReporteDtos.ReporteData;
import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.PartidaRepository;
import com.example.CodeMan.repository.ProgresoRepository;
import com.example.CodeMan.repository.ReporteRepository;
import com.example.CodeMan.repository.RespuestaPartidaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final ProgresoRepository progresoRepository;
    private final PartidaRepository partidaRepository;
    private final RespuestaPartidaRepository respuestaPartidaRepository;
    private final PdfGenerator pdfGenerator;

    public ReporteService(ReporteRepository reporteRepository,
                          ProgresoRepository progresoRepository,
                          PartidaRepository partidaRepository,
                          RespuestaPartidaRepository respuestaPartidaRepository,
                          PdfGenerator pdfGenerator) {
        this.reporteRepository = reporteRepository;
        this.progresoRepository = progresoRepository;
        this.partidaRepository = partidaRepository;
        this.respuestaPartidaRepository = respuestaPartidaRepository;
        this.pdfGenerator = pdfGenerator;
    }

    public ReporteData construirDatos(Usuario usuario) {
        long totalPartidas = partidaRepository.countByUsuario(usuario);
        long aciertosTotal = respuestaPartidaRepository.countByPartida_UsuarioAndEsCorrecta(usuario, true);
        long erroresTotal = respuestaPartidaRepository.countByPartida_UsuarioAndEsCorrecta(usuario, false);
        long totalRespuestas = aciertosTotal + erroresTotal;
        int porcentajeGeneral = totalRespuestas == 0 ? 0
                : (int) Math.round((aciertosTotal * 100.0) / totalRespuestas);

        List<DesempenoPorCategoria> porCategoria = new ArrayList<>();
        for (Categoria cat : new Categoria[]{ Categoria.CREACIONAL, Categoria.ESTRUCTURAL, Categoria.COMPORTAMIENTO }) {
            long catAciertos = respuestaPartidaRepository
                    .countByPartida_UsuarioAndPregunta_CategoriaAndEsCorrecta(usuario, cat, true);
            long catErrores = respuestaPartidaRepository
                    .countByPartida_UsuarioAndPregunta_CategoriaAndEsCorrecta(usuario, cat, false);
            long catTotal = catAciertos + catErrores;
            int catPct = catTotal == 0 ? 0 : (int) Math.round((catAciertos * 100.0) / catTotal);
            porCategoria.add(new DesempenoPorCategoria(cat.name(), (int) catAciertos, (int) catErrores, catPct));
        }

        return new ReporteData(
                usuario.getName(),
                porcentajeGeneral,
                (int) totalPartidas,
                (int) aciertosTotal,
                (int) erroresTotal,
                0,
                "—",
                porCategoria,
                LocalDateTime.now());
    }

    public byte[] generarPdf(Usuario usuario) {
        ReporteData datos = construirDatos(usuario);
        return pdfGenerator.generar(datos);
    }
}
