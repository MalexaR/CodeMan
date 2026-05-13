package com.example.CodeMan.repository;

import com.example.CodeMan.model.Reporte;
import com.example.CodeMan.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByUsuarioOrderByFechaGeneracionDesc(Usuario usuario);

    Optional<Reporte> findTopByUsuarioOrderByFechaGeneracionDesc(Usuario usuario);
}
