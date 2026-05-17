package com.example.CodeMan.service;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Pregunta;
import com.example.CodeMan.repository.PreguntaRepository;
import com.example.CodeMan.repository.RespuestaPartidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final RespuestaPartidaRepository respuestaPartidaRepository;

    public PreguntaService(PreguntaRepository preguntaRepository,
            RespuestaPartidaRepository respuestaPartidaRepository) {
        this.preguntaRepository = preguntaRepository;
        this.respuestaPartidaRepository = respuestaPartidaRepository;
    }

    public List<Pregunta> listar() {
        return preguntaRepository.findAll();
    }

    public List<Pregunta> porCategoria(Categoria categoria) {
        return preguntaRepository.findByCategoria(categoria);
    }

    public Pregunta buscar(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada: " + id));
    }

    public long total() {
        return preguntaRepository.count();
    }

    @Transactional
    public Pregunta guardar(Pregunta pregunta) {
        return preguntaRepository.save(pregunta);
    }

    @Transactional
    public void eliminar(Long id) {
        preguntaRepository.deleteById(id);
    }

    public boolean posibleDuplicado(String enunciado, Long excluirId) {
        String trimmed = enunciado == null ? "" : enunciado.trim();
        if (excluirId != null) {
            return preguntaRepository.existsByEnunciadoIgnoreCaseAndIdNot(trimmed, excluirId);
        }
        return preguntaRepository.existsByEnunciadoIgnoreCase(trimmed);
    }

    public boolean tieneRespuestas(Long preguntaId) {
        return respuestaPartidaRepository.existsByPreguntaId(preguntaId);
    }
}
