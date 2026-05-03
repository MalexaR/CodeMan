package com.example.CodeMan.repository;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {
    List<Pregunta> findByCategoria(Categoria categoria);
    List<Pregunta> findByCategoriaAndActivaTrue(Categoria categoria);
}
