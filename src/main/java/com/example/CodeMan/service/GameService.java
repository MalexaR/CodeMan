package com.example.CodeMan.service;

import com.example.CodeMan.dto.GameDtos.*;
import com.example.CodeMan.model.*;
import com.example.CodeMan.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final PreguntaRepository preguntaRepository;
    private final PartidaRepository partidaRepository;
    private final RespuestaPartidaRepository respuestaPartidaRepository;
    private final ProgresoRepository progresoRepository;

    public GameService(PreguntaRepository preguntaRepository,
            PartidaRepository partidaRepository,
            RespuestaPartidaRepository respuestaPartidaRepository,
            ProgresoRepository progresoRepository) {
        this.preguntaRepository = preguntaRepository;
        this.partidaRepository = partidaRepository;
        this.respuestaPartidaRepository = respuestaPartidaRepository;
        this.progresoRepository = progresoRepository;
    }

    public List<PreguntaDto> preguntasPorCategoria(Categoria categoria) {
        List<Pregunta> preguntas = preguntaRepository.findByCategoriaAndActivaTrue(categoria);
        Collections.shuffle(preguntas);
        return preguntas.stream()
            .map(p -> new PreguntaDto(
                p.getId(),
                p.getEnunciado(),
                p.getOpcionA(),
                p.getOpcionB(),
                p.getOpcionC(),
                p.getOpcionD(),
                p.getRespuestaCorrecta() != null ? p.getRespuestaCorrecta().name() : null,
                p.getCategoria() != null ? p.getCategoria().name() : null,
                p.getDificultad() != null ? p.getDificultad().name() : null,
                p.getTipoEvento() != null ? p.getTipoEvento().name() : null
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public StartGameResponse iniciar(Usuario usuario, StartGameRequest request) {
        Partida partida = new Partida();
        partida.setUsuario(usuario);
        partida.setCategoria(request.categoria());
        partida.setModoJuego(request.modoJuego() == null ? ModoJuego.PRACTICA : request.modoJuego());
        partida.setVidas(3);
        partida.setPuntuacion(0);
        partida.setCompletada(false);
        partida.setErroresAcumulados(0);
        partida.setFantasmaExtraActivo(false);
        partidaRepository.save(partida);
        return new StartGameResponse(partida.getId());
    }

    @Transactional
    public AnswerResponse responder(Usuario usuario, AnswerRequest request) {
        Partida partida = partidaRepository.findById(request.partidaId())
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));
        if (!partida.getUsuario().getId().equals(usuario.getId()))
            throw new IllegalArgumentException("Partida no pertenece al usuario actual");

        Pregunta pregunta = preguntaRepository.findById(request.preguntaId())
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada"));

        RespuestaPartida respuesta = new RespuestaPartida();
        respuesta.setPartida(partida);
        respuesta.setPregunta(pregunta);
        respuesta.setOpcionElegida(request.opcionElegida());
        respuesta.setEsCorrecta(request.esCorrecta());
        respuesta.setTiempoRespuesta(request.tiempoRespuesta());
        respuestaPartidaRepository.save(respuesta);

        int nuevosErrores = partida.getErroresAcumulados() + (request.esCorrecta() ? 0 : 1);
        int nuevaPuntuacion = partida.getPuntuacion() + (request.esCorrecta() ? 100 : 0);
        partida.setErroresAcumulados(nuevosErrores);
        partida.setFantasmaExtraActivo(nuevosErrores >= 3);
        partida.setPuntuacion(nuevaPuntuacion);
        partidaRepository.save(partida);

        return new AnswerResponse(true, nuevosErrores, partida.isFantasmaExtraActivo(), nuevaPuntuacion);
    }

    @Transactional
    public SimpleResponse finalizar(Usuario usuario, FinishGameRequest request) {
        Partida partida = partidaRepository.findById(request.partidaId())
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));
        if (!partida.getUsuario().getId().equals(usuario.getId()))
            throw new IllegalArgumentException("Partida no pertenece al usuario actual");

        partida.setFechaFin(LocalDateTime.now());
        partida.setCompletada(true);
        partida.setPuntuacion(request.puntuacion());
        partida.setVidas(request.vidas());
        partidaRepository.save(partida);

        Progreso progreso = progresoRepository.findByUsuario(usuario).orElseGet(() -> {
            Progreso p = new Progreso();
            p.setUsuario(usuario);
            return p;
        });
        progreso.setPartidasJugadas(progreso.getPartidasJugadas() + 1);
        progreso.setUltimoAcceso(LocalDateTime.now());
        progresoRepository.save(progreso);

        return new SimpleResponse(true);
    }

    @Transactional
    public SimpleResponse actualizarVidas(Usuario usuario, UpdateLivesRequest request) {
        Partida partida = partidaRepository.findById(request.partidaId())
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));
        if (!partida.getUsuario().getId().equals(usuario.getId()))
            throw new IllegalArgumentException("Partida no pertenece al usuario actual");
        partida.setVidas(request.vidas());
        partidaRepository.save(partida);
        return new SimpleResponse(true);
    }
}
