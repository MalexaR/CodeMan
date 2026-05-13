package com.example.CodeMan.repository;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Partida;
import com.example.CodeMan.model.RespuestaPartida;
import com.example.CodeMan.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaPartidaRepository extends JpaRepository<RespuestaPartida, Long> {
    long countByPartida_UsuarioAndEsCorrecta(Usuario usuario, boolean esCorrecta);
    long countByPartida(Partida partida);
    long countByPartidaAndEsCorrecta(Partida partida, boolean esCorrecta);
    List<RespuestaPartida> findByPartida(Partida partida);
    List<RespuestaPartida> findByPartidaAndEsCorrecta(Partida partida, boolean esCorrecta);
    long countByPartida_UsuarioAndPregunta_CategoriaAndEsCorrecta(Usuario usuario, Categoria categoria, boolean esCorrecta);
}
