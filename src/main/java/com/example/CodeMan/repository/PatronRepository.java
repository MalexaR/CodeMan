package com.example.CodeMan.repository;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Patron;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PatronRepository extends JpaRepository<Patron, Long> {
    List<Patron> findByCategoria(Categoria categoria);
}
