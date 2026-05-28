package com.example.CodeMan.controller;

import com.example.CodeMan.dto.GameDtos.AnswerRequest;
import com.example.CodeMan.dto.GameDtos.AnswerResponse;
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
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.GameService;
import com.example.CodeMan.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameApiController {

    private static final Logger log = LoggerFactory.getLogger(GameApiController.class);

    private final GameService gameService;
    private final UsuarioService usuarioService;

    public GameApiController(GameService gameService, UsuarioService usuarioService) {
        this.gameService = gameService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/questions")
    public List<PreguntaDto> preguntas(@RequestParam Categoria categoria) {
        return gameService.preguntasPorCategoria(categoria);
    }

    @GetMapping("/questions/desafio")
    public List<PreguntaDto> preguntasDesafioFinal() {
        return gameService.preguntasDesafioFinal();
    }

    @PostMapping("/start")
    public StartGameResponse iniciar(@RequestBody StartGameRequest request, Authentication authentication) {
        return gameService.iniciar(usuarioService.actual(authentication), request);
    }

    @PostMapping("/answer")
    public AnswerResponse responder(@RequestBody AnswerRequest request, Authentication authentication) {
        try {
            return gameService.responder(usuarioService.actual(authentication), request);
        } catch (RuntimeException ex) {
            log.error("Error guardando respuesta partida={} pregunta={} opcion={}: {}",
                    request.partidaId(), request.preguntaId(), request.opcionElegida(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @PostMapping("/finish")
    public SimpleResponse finalizar(@RequestBody FinishGameRequest request, Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        try {
            com.example.CodeMan.model.Partida partida = gameService.cerrarPartida(usuario, request);
            try {
                gameService.actualizarProgresoFinPartida(usuario, partida.getId());
            } catch (RuntimeException ex) {
                log.warn("Partida {} cerrada pero el progreso no se actualizo: {}",
                        partida.getId(), ex.getMessage(), ex);
            }
            return new SimpleResponse(true);
        } catch (RuntimeException ex) {
            log.error("Error cerrando partida={}: {}", request.partidaId(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @PostMapping("/lives")
    public SimpleResponse vidas(@RequestBody UpdateLivesRequest request, Authentication authentication) {
        return gameService.actualizarVidas(usuarioService.actual(authentication), request);
    }

    @GetMapping("/{partidaId}/resultado")
    public ResultadoPartidaDto resultado(@PathVariable Long partidaId, Authentication authentication) {
        return gameService.resultado(usuarioService.actual(authentication), partidaId);
    }

    @GetMapping("/eventos")
    public List<EventoDecisionDto> eventos(@RequestParam Categoria categoria) {
        return gameService.eventosDecisionCritica(categoria);
    }

    @PostMapping("/evento")
    public EventoDecisionResponse resolverEvento(@RequestBody EventoDecisionRequest request,
            Authentication authentication) {
        return gameService.resolverEvento(usuarioService.actual(authentication), request);
    }
}
