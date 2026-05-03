package com.example.CodeMan.service;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Ficha;
import com.example.CodeMan.model.FichaRevisada;
import com.example.CodeMan.model.Patron;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.FichaRepository;
import com.example.CodeMan.repository.FichaRevisadaRepository;
import com.example.CodeMan.repository.PatronRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatronService {

    private final PatronRepository patronRepository;
    private final FichaRepository fichaRepository;
    private final FichaRevisadaRepository fichaRevisadaRepository;

    public PatronService(PatronRepository patronRepository,
            FichaRepository fichaRepository,
            FichaRevisadaRepository fichaRevisadaRepository) {
        this.patronRepository = patronRepository;
        this.fichaRepository = fichaRepository;
        this.fichaRevisadaRepository = fichaRevisadaRepository;
    }

    public List<Patron> listar() {
        return patronRepository.findAll();
    }

    public List<Patron> porCategoria(Categoria categoria) {
        return patronRepository.findByCategoria(categoria);
    }

    public Patron buscar(Long id) {
        return patronRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patrón no encontrado"));
    }

    public long total() {
        return fichaRepository.count();
    }

    public long revisadasPor(Usuario usuario) {
        return fichaRevisadaRepository.countByUsuario(usuario);
    }

    public boolean estaRevisado(Usuario usuario, Patron patron) {
        return fichaRepository.findByPatronId(patron.getId())
                .map(ficha -> fichaRevisadaRepository.existsByUsuarioAndFicha(usuario, ficha))
                .orElse(false);
    }

    @Transactional
    public void marcarRevisado(Usuario usuario, Long patronId) {
        Patron patron = buscar(patronId);

        Ficha ficha = fichaRepository.findByPatronId(patron.getId())
                .orElseThrow(() -> new IllegalArgumentException("Ficha no encontrada para este patrón"));

        FichaRevisada registro = fichaRevisadaRepository.findByUsuarioAndFicha(usuario, ficha)
                .orElseGet(() -> {
                    FichaRevisada nueva = new FichaRevisada();
                    nueva.setUsuario(usuario);
                    nueva.setFicha(ficha);
                    return nueva;
                });

        registro.setFechaRevision(LocalDateTime.now());
        fichaRevisadaRepository.save(registro);
    }

    @Transactional
    public Patron guardar(Patron patron) {
        return patronRepository.save(patron);
    }
}