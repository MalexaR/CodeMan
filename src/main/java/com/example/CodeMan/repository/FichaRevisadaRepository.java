package com.example.CodeMan.repository;

import com.example.CodeMan.model.Ficha;
import com.example.CodeMan.model.FichaRevisada;
import com.example.CodeMan.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FichaRevisadaRepository extends JpaRepository<FichaRevisada, Long> {

    List<FichaRevisada> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndFichaId(Long usuarioId, Long fichaId);

    long countByUsuarioId(Long usuarioId);

    Optional<FichaRevisada> findByUsuarioAndFicha(Usuario usuario, Ficha ficha);

    boolean existsByUsuarioAndFicha(Usuario usuario, Ficha ficha);

    long countByUsuario(Usuario usuario);
}