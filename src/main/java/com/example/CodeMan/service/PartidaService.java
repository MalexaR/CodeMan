package com.example.CodeMan.service;

import com.example.CodeMan.model.Partida;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.PartidaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;

    public PartidaService(PartidaRepository partidaRepository) {
        this.partidaRepository = partidaRepository;
    }

    public List<Partida> historialDeUsuario(Usuario usuario) {
        return null; // TODO
    }

    public List<Partida> topPorPuntuacion(int limite) {
        return null; // TODO
    }

    public long totalDeUsuario(Usuario usuario) {
        return 0; // TODO
    }
}
