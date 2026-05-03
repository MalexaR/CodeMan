package com.example.CodeMan.repository;

import com.example.CodeMan.model.Progreso;
import com.example.CodeMan.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProgresoRepository extends JpaRepository<Progreso, Long> {
    Optional<Progreso> findByUsuario(Usuario usuario);
}
