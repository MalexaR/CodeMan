package com.example.CodeMan.service;

import com.example.CodeMan.dto.GameDtos.AnswerRequest;
import com.example.CodeMan.dto.GameDtos.AnswerResponse;
import com.example.CodeMan.dto.GameDtos.ErrorDetalle;
import com.example.CodeMan.dto.GameDtos.EventoDecisionDto;
import com.example.CodeMan.dto.GameDtos.EventoDecisionRequest;
import com.example.CodeMan.dto.GameDtos.EventoDecisionResponse;
import com.example.CodeMan.dto.GameDtos.FinishGameRequest;
import com.example.CodeMan.dto.GameDtos.PreguntaDto;
import com.example.CodeMan.dto.GameDtos.ResultadoPartidaDto;
import com.example.CodeMan.dto.GameDtos.SimpleResponse;
import com.example.CodeMan.dto.GameDtos.StartGameRequest;
import com.example.CodeMan.dto.GameDtos.StartGameResponse;
import com.example.CodeMan.dto.GameDtos.UpdateLivesRequest;
import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.ModoJuego;
import com.example.CodeMan.model.Partida;
import com.example.CodeMan.model.Pregunta;
import com.example.CodeMan.model.Progreso;
import com.example.CodeMan.model.RespuestaPartida;
import com.example.CodeMan.model.TipoEvento;
import com.example.CodeMan.model.TipoPowerUp;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.PartidaRepository;
import com.example.CodeMan.repository.PreguntaRepository;
import com.example.CodeMan.repository.ProgresoRepository;
import com.example.CodeMan.repository.RespuestaPartidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {
    private final PreguntaRepository preguntaRepository;
    private final PartidaRepository partidaRepository;
    private final RespuestaPartidaRepository respuestaPartidaRepository;
    private final ProgresoRepository progresoRepository;
    private final ScormReporter scormReporter;

    public GameService(PreguntaRepository preguntaRepository,
            PartidaRepository partidaRepository,
            RespuestaPartidaRepository respuestaPartidaRepository,
            ProgresoRepository progresoRepository,
            ScormReporter scormReporter) {
        this.preguntaRepository = preguntaRepository;
        this.partidaRepository = partidaRepository;
        this.respuestaPartidaRepository = respuestaPartidaRepository;
        this.progresoRepository = progresoRepository;
        this.scormReporter = scormReporter;
    }

    public List<PreguntaDto> preguntasPorCategoria(Categoria categoria) {
        List<Pregunta> preguntas = preguntaRepository
                .findByCategoriaAndTipoEventoAndActivaTrue(categoria, TipoEvento.PREGUNTA_NORMAL);
        Collections.shuffle(preguntas);
        return preguntas.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<PreguntaDto> preguntasDesafioFinal() {
        List<Pregunta> todas = new ArrayList<>();
        todas.addAll(preguntaRepository.findByCategoriaAndTipoEventoAndActivaTrue(Categoria.CREACIONAL, TipoEvento.PREGUNTA_NORMAL));
        todas.addAll(preguntaRepository.findByCategoriaAndTipoEventoAndActivaTrue(Categoria.ESTRUCTURAL, TipoEvento.PREGUNTA_NORMAL));
        todas.addAll(preguntaRepository.findByCategoriaAndTipoEventoAndActivaTrue(Categoria.COMPORTAMIENTO, TipoEvento.PREGUNTA_NORMAL));
        Collections.shuffle(todas);
        return todas.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<EventoDecisionDto> eventosDecisionCritica(Categoria categoria) {
        List<Pregunta> eventos = preguntaRepository
                .findByCategoriaAndTipoEventoAndActivaTrue(categoria, TipoEvento.DECISION_CRITICA);
        Collections.shuffle(eventos);
        return eventos.stream()
                .map(p -> new EventoDecisionDto(
                        p.getId(),
                        p.getEnunciado(),
                        p.getOpcionA(),
                        p.getOpcionB(),
                        p.getOpcionC(),
                        p.getRespuestaCorrecta() != null ? p.getRespuestaCorrecta().name() : null,
                        null))
                .collect(Collectors.toList());
    }

    private PreguntaDto toDto(Pregunta p) {
        return new PreguntaDto(
                p.getId(),
                p.getEnunciado(),
                p.getOpcionA(),
                p.getOpcionB(),
                p.getOpcionC(),
                p.getOpcionD(),
                p.getRespuestaCorrecta() != null ? p.getRespuestaCorrecta().name() : null,
                p.getCategoria() != null ? p.getCategoria().name() : null,
                p.getDificultad() != null ? p.getDificultad().name() : null,
                p.getTipoEvento() != null ? p.getTipoEvento().name() : null);
    }

    @Transactional
    public StartGameResponse iniciar(Usuario usuario, StartGameRequest request) {
        Partida partida = new Partida();
        partida.setUsuario(usuario);
        partida.setCategoria(request.categoria());
        partida.setModoJuego(request.modoJuego() == null ? ModoJuego.PRACTICA : request.modoJuego());
        partida.setVidasRestantes(3);
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
        if (request.opcionElegida() == null) {
            respuesta.setOpcionElegida(null);
            respuesta.setTimeout(true);
        } else {
            respuesta.setOpcionElegida(request.opcionElegida());
            respuesta.setTimeout(false);
        }
        respuesta.setEsCorrecta(request.esCorrecta());
        respuesta.setTiempoRespuestaMs(request.tiempoRespuesta());
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
    public EventoDecisionResponse resolverEvento(Usuario usuario, EventoDecisionRequest request) {
        Partida partida = partidaRepository.findById(request.partidaId())
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));
        if (!partida.getUsuario().getId().equals(usuario.getId()))
            throw new IllegalArgumentException("Partida no pertenece al usuario actual");

        Pregunta pregunta = preguntaRepository.findById(request.eventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento no encontrado"));

        boolean esCorrecta = request.opcionElegida() != null
                && !"TIMEOUT".equals(request.opcionElegida())
                && pregunta.getRespuestaCorrecta() != null
                && request.opcionElegida().equals(pregunta.getRespuestaCorrecta().name());

        String powerUpAplicado = null;
        String penalizacionAplicada = null;

        if (esCorrecta) {
            TipoPowerUp[] values = TipoPowerUp.values();
            powerUpAplicado = values[(int) (Math.random() * values.length)].name();
        } else {
            penalizacionAplicada = "PIERDE_VIDA";
            partida.setVidasRestantes(Math.max(0, partida.getVidasRestantes() - 1));
            partidaRepository.save(partida);
        }

        return new EventoDecisionResponse(esCorrecta, powerUpAplicado, penalizacionAplicada);
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
        partida.setVidasRestantes(request.vidas());
        partidaRepository.save(partida);

        Progreso progreso = progresoRepository.findByUsuario(usuario).orElseGet(() -> {
            Progreso p = new Progreso();
            p.setUsuario(usuario);
            return p;
        });
        progreso.setUltimoAcceso(LocalDateTime.now());

        long total = respuestaPartidaRepository.countByPartida(partida);
        long correctas = respuestaPartidaRepository.countByPartidaAndEsCorrecta(partida, true);

        if (partida.getModoJuego() == ModoJuego.DESAFIO
                && total > 0 && (correctas * 100.0 / total) >= 60.0) {
            progreso.setDesafioFinalCompletado(true);
        }

        progresoRepository.save(progreso);

        int porcentaje = total == 0 ? 0 : (int) Math.round((correctas * 100.0) / total);
        scormReporter.registrar(partida.getId(), porcentaje);

        return new SimpleResponse(true);
    }

    public ResultadoPartidaDto resultado(Usuario usuario, Long partidaId) {
        Partida partida = partidaRepository.findById(partidaId)
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));
        if (!partida.getUsuario().getId().equals(usuario.getId()))
            throw new IllegalArgumentException("Partida no pertenece al usuario actual");

        List<RespuestaPartida> respuestas = respuestaPartidaRepository.findByPartida(partida);
        List<RespuestaPartida> incorrectas = respuestaPartidaRepository.findByPartidaAndEsCorrecta(partida, false);

        int total = respuestas.size();
        int aciertos = (int) respuestas.stream().filter(RespuestaPartida::isEsCorrecta).count();
        int errores = total - aciertos;
        int porcentaje = total == 0 ? 0 : (int) Math.round((aciertos * 100.0) / total);

        String tiempoTotal = "—";
        if (partida.getFechaInicio() != null && partida.getFechaFin() != null) {
            long seg = Duration.between(partida.getFechaInicio(), partida.getFechaFin()).getSeconds();
            tiempoTotal = String.format("%02d:%02d", seg / 60, seg % 60);
        }

        boolean victoria = partida.getVidasRestantes() > 0;
        String categoria = partida.getCategoria() != null ? partida.getCategoria().name() : "DESAFIO";

        List<ErrorDetalle> erroresDetalle = incorrectas.stream()
                .map(r -> new ErrorDetalle(
                        r.getPregunta().getEnunciado(),
                        r.getOpcionElegida() != null ? r.getOpcionElegida().name() : "—",
                        r.getPregunta().getRespuestaCorrecta() != null ? r.getPregunta().getRespuestaCorrecta().name() : "—",
                        r.getPregunta().getExplicacion()))
                .collect(Collectors.toList());

        return new ResultadoPartidaDto(partida.getId(), total, aciertos, errores,
                porcentaje, tiempoTotal, categoria, victoria, erroresDetalle);
    }

    @Transactional
    public SimpleResponse actualizarVidas(Usuario usuario, UpdateLivesRequest request) {
        Partida partida = partidaRepository.findById(request.partidaId())
                .orElseThrow(() -> new IllegalArgumentException("Partida no encontrada"));
        if (!partida.getUsuario().getId().equals(usuario.getId()))
            throw new IllegalArgumentException("Partida no pertenece al usuario actual");
        partida.setVidasRestantes(request.vidas());
        partidaRepository.save(partida);
        return new SimpleResponse(true);
    }
}
