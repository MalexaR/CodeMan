package com.example.CodeMan.controller;

import com.example.CodeMan.dto.GameDtos.*;
import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.service.GameService;
import com.example.CodeMan.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameApiController {
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

    @PostMapping("/start")
    public StartGameResponse iniciar(@RequestBody StartGameRequest request, Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        return gameService.iniciar(usuario, request);
    }

    @PostMapping("/answer")
    public AnswerResponse responder(@RequestBody AnswerRequest request, Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        return gameService.responder(usuario, request);
    }

    @PostMapping("/finish")
    public SimpleResponse finalizar(@RequestBody FinishGameRequest request, Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        return gameService.finalizar(usuario, request);
    }

    @PostMapping("/lives")
    public SimpleResponse vidas(@RequestBody UpdateLivesRequest request, Authentication authentication) {
        Usuario usuario = usuarioService.actual(authentication);
        return gameService.actualizarVidas(usuario, request);
    }
}
