package com.example.CodeMan.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ReporteDtos {

    public record DesempenoPorCategoria(
        String categoria,
        int aciertos,
        int errores,
        int porcentaje
    ) {}

    public record ReporteData(
        String nombreEstudiante,
        int porcentajeGeneral,
        int totalPartidas,
        int aciertosTotal,
        int erroresTotal,
        int puntuacionDesafioFinal,
        String tiempoDesafioFinal,
        List<DesempenoPorCategoria> porCategoria,
        LocalDateTime fechaGeneracion
    ) {}
}
