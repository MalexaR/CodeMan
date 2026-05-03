package com.example.CodeMan.repository;

import com.example.CodeMan.model.Partida;
import com.example.CodeMan.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByUsuarioOrderByFechaInicioDesc(Usuario usuario);

    long countByUsuario(Usuario usuario);
}
