package com.example.CodeMan.repository;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatronRepository extends JpaRepository<Patron, Long> {
    List<Patron> findByCategoria(Categoria categoria);
    Optional<Patron> findFirstByCategoriaAndIdLessThanOrderByIdDesc(Categoria categoria, Long id);
    Optional<Patron> findFirstByCategoriaAndIdGreaterThanOrderByIdAsc(Categoria categoria, Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
