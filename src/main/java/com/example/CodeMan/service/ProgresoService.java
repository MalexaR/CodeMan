package com.example.CodeMan.service;

import com.example.CodeMan.model.Progreso;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.PartidaRepository;
import com.example.CodeMan.repository.PatronRepository;
import com.example.CodeMan.repository.PatronRevisadoRepository;
import com.example.CodeMan.repository.ProgresoRepository;
import com.example.CodeMan.repository.RespuestaPartidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProgresoService {

    private final PatronRepository patronRepository;
    private final PatronRevisadoRepository patronRevisadoRepository;
    private final PartidaRepository partidaRepository;
    private final RespuestaPartidaRepository respuestaPartidaRepository;
    private final ProgresoRepository progresoRepository;

    public ProgresoService(PatronRepository patronRepository,
            PatronRevisadoRepository patronRevisadoRepository,
            PartidaRepository partidaRepository,
            RespuestaPartidaRepository respuestaPartidaRepository,
            ProgresoRepository progresoRepository) {
        this.patronRepository = patronRepository;
        this.patronRevisadoRepository = patronRevisadoRepository;
        this.partidaRepository = partidaRepository;
        this.respuestaPartidaRepository = respuestaPartidaRepository;
        this.progresoRepository = progresoRepository;
    }

    @Transactional
    public Progreso resumen(Usuario usuario) {
        Progreso progreso = progresoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Progreso nuevo = new Progreso();
                    nuevo.setUsuario(usuario);
                    return nuevo;
                });

        progreso.setPatronesRevisadosCache((int) patronRevisadoRepository.countByUsuario(usuario));
        progreso.setPartidasJugadasCache((int) partidaRepository.countByUsuario(usuario));
        progreso.setAciertosCache((int) respuestaPartidaRepository.countByPartida_UsuarioAndEsCorrecta(usuario, true));
        progreso.setErroresCache((int) respuestaPartidaRepository.countByPartida_UsuarioAndEsCorrecta(usuario, false));
        progreso.setUltimoAcceso(LocalDateTime.now());
        progresoRepository.save(progreso);

        progreso.setTotalPatrones(patronRepository.count());

        return progreso;
    }
}
