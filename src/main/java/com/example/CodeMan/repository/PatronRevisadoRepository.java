package com.example.CodeMan.repository;

import com.example.CodeMan.model.Patron;
import com.example.CodeMan.model.PatronRevisado;
import com.example.CodeMan.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatronRevisadoRepository extends JpaRepository<PatronRevisado, Long> {

    boolean existsByUsuarioAndPatron(Usuario usuario, Patron patron);

    long countByUsuario(Usuario usuario);

    List<PatronRevisado> findByUsuarioId(Long usuarioId);
}
