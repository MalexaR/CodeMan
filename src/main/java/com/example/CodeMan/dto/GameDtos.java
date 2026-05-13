package com.example.CodeMan.dto;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.ModoJuego;
import com.example.CodeMan.model.OpcionRespuesta;

public class GameDtos {

    public record PreguntaDto(
        Long id,
        String enunciado,
        String opcionA,
        String opcionB,
        String opcionC,
        String opcionD,
        String respuestaCorrecta,
        String categoria,
        String dificultad,
        String tipoEvento
    ) {}

    public record StartGameRequest(Categoria categoria, ModoJuego modoJuego) {
    }

    public record StartGameResponse(Long partidaId) {
    }

    public record AnswerRequest(Long partidaId, Long preguntaId, OpcionRespuesta opcionElegida, int tiempoRespuesta,
            boolean esCorrecta) {
    }

    public record AnswerResponse(boolean success, int erroresAcumulados, boolean fantasmaExtraActivo, int puntuacion) {
    }

    public record FinishGameRequest(Long partidaId, int puntuacion, int vidas) {
    }

    public record UpdateLivesRequest(Long partidaId, int vidas) {
    }

    public record SimpleResponse(boolean success) {
    }

    // HU-07: Evento de decisión crítica
    public record EventoDecisionDto(
        Long id,
        String escenario,
        String opcionA,
        String opcionB,
        String opcionC,
        String respuestaCorrecta,
        String tipoPowerUpRecompensa
    ) {}

    public record EventoDecisionRequest(Long partidaId, Long eventoId, String opcionElegida) {
    }

    public record EventoDecisionResponse(boolean esCorrecta, String powerUpAplicado, String penalizacionAplicada) {
    }

    // HU-08: Retroalimentación al finalizar partida
    public record ErrorDetalle(
        String enunciado,
        String opcionElegida,
        String respuestaCorrecta,
        String explicacion
    ) {}

    public record ResultadoPartidaDto(
        Long partidaId,
        int totalPreguntas,
        int aciertos,
        int errores,
        int porcentajeDesempeno,
        String tiempoTotal,
        String categoria,
        boolean victoria,
        java.util.List<ErrorDetalle> erroresDetalle
    ) {}
}
