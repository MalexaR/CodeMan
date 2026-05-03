package com.example.CodeMan.repository;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Ficha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FichaRepository extends JpaRepository<Ficha, Long> {

    Optional<Ficha> findByPatronId(Long patronId);

    List<Ficha> findByPatronCategoria(Categoria categoria);
}