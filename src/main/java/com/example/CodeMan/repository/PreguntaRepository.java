package com.example.CodeMan.repository;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Pregunta;
import com.example.CodeMan.model.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByCategoria(Categoria categoria);
    List<Pregunta> findByCategoriaAndActivaTrue(Categoria categoria);
    List<Pregunta> findByCategoriaAndTipoEventoAndActivaTrue(Categoria categoria, TipoEvento tipoEvento);

    boolean existsByPatronId(Long patronId);

    long countByPatronId(Long patronId);

    boolean existsByEnunciadoIgnoreCase(String enunciado);

    boolean existsByEnunciadoIgnoreCaseAndIdNot(String enunciado, Long id);
}
