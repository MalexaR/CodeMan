package com.example.CodeMan.repository;

import com.example.CodeMan.model.RespuestaPartida;
import com.example.CodeMan.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespuestaPartidaRepository extends JpaRepository<RespuestaPartida, Long> {
    long countByPartida_UsuarioAndEsCorrecta(Usuario usuario, boolean esCorrecta);
}
