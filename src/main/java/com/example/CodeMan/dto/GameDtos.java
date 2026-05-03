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
}
