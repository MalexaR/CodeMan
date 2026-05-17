package com.example.CodeMan.service;

import com.example.CodeMan.model.Categoria;
import com.example.CodeMan.model.Patron;
import com.example.CodeMan.model.PatronRevisado;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.PatronRepository;
import com.example.CodeMan.repository.PatronRevisadoRepository;
import com.example.CodeMan.repository.PreguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PatronService {

    private final PatronRepository patronRepository;
    private final PatronRevisadoRepository patronRevisadoRepository;
    private final PreguntaRepository preguntaRepository;

    public PatronService(PatronRepository patronRepository,
            PatronRevisadoRepository patronRevisadoRepository,
            PreguntaRepository preguntaRepository) {
        this.patronRepository = patronRepository;
        this.patronRevisadoRepository = patronRevisadoRepository;
        this.preguntaRepository = preguntaRepository;
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
        return patronRepository.count();
    }

    public long revisadosPor(Usuario usuario) {
        return patronRevisadoRepository.countByUsuario(usuario);
    }

    public boolean estaRevisado(Usuario usuario, Patron patron) {
        return patronRevisadoRepository.existsByUsuarioAndPatron(usuario, patron);
    }

    @Transactional
    public void marcarRevisado(Usuario usuario, Long patronId) {
        Patron patron = buscar(patronId);
        if (!patronRevisadoRepository.existsByUsuarioAndPatron(usuario, patron)) {
            PatronRevisado registro = new PatronRevisado();
            registro.setUsuario(usuario);
            registro.setPatron(patron);
            patronRevisadoRepository.save(registro);
        }
    }

    @Transactional
    public Patron guardar(Patron patron) {
        return patronRepository.save(patron);
    }

    @Transactional
    public void eliminar(Long id) {
        patronRevisadoRepository.deleteByPatronId(id);
        patronRepository.deleteById(id);
    }

    public Optional<Patron> anterior(Patron patron) {
        return patronRepository.findFirstByCategoriaAndIdLessThanOrderByIdDesc(patron.getCategoria(), patron.getId());
    }

    public Optional<Patron> siguiente(Patron patron) {
        return patronRepository.findFirstByCategoriaAndIdGreaterThanOrderByIdAsc(patron.getCategoria(), patron.getId());
    }

    /** Retorna mensaje de advertencia si el patrón tiene dependencias que bloquean su eliminación, o null si puede borrarse. */
    public String advertenciaEliminar(Long id) {
        long preguntas = preguntaRepository.countByPatronId(id);
        if (preguntas > 0) {
            return "Este patrón tiene " + preguntas + " pregunta(s) asociada(s). Elimínalas primero desde el banco de preguntas.";
        }
        return null;
    }

    public boolean posibleDuplicado(String nombre, Long excluirId) {
        String trimmed = nombre == null ? "" : nombre.trim();
        if (excluirId != null) {
            return patronRepository.existsByNombreIgnoreCaseAndIdNot(trimmed, excluirId);
        }
        return patronRepository.existsByNombreIgnoreCase(trimmed);
    }
}
