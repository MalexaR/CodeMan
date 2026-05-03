package com.example.CodeMan.service;

import com.example.CodeMan.model.Progreso;
import com.example.CodeMan.model.Usuario;
import com.example.CodeMan.repository.FichaRepository;
import com.example.CodeMan.repository.FichaRevisadaRepository;
import com.example.CodeMan.repository.PartidaRepository;
import com.example.CodeMan.repository.ProgresoRepository;
import com.example.CodeMan.repository.RespuestaPartidaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProgresoService {

    private final FichaRepository fichaRepository;
    private final FichaRevisadaRepository fichaRevisadaRepository;
    private final PartidaRepository partidaRepository;
    private final RespuestaPartidaRepository respuestaPartidaRepository;
    private final ProgresoRepository progresoRepository;

    public ProgresoService(FichaRepository fichaRepository,
            FichaRevisadaRepository fichaRevisadaRepository,
            PartidaRepository partidaRepository,
            RespuestaPartidaRepository respuestaPartidaRepository,
            ProgresoRepository progresoRepository) {
        this.fichaRepository = fichaRepository;
        this.fichaRevisadaRepository = fichaRevisadaRepository;
        this.partidaRepository = partidaRepository;
        this.respuestaPartidaRepository = respuestaPartidaRepository;
        this.progresoRepository = progresoRepository;
    }

    public ProgresoResumen resumen(Usuario usuario) {
        ProgresoResumen r = new ProgresoResumen();

        r.setTotalFichas(fichaRepository.count());
        r.setFichasRevisadas(fichaRevisadaRepository.countByUsuario(usuario));
        r.setPartidasJugadas(partidaRepository.countByUsuario(usuario));
        r.setAciertos(respuestaPartidaRepository.countByPartida_UsuarioAndEsCorrecta(usuario, true));
        r.setErrores(respuestaPartidaRepository.countByPartida_UsuarioAndEsCorrecta(usuario, false));
        r.setDesafioFinalCompletado(
                progresoRepository.findByUsuario(usuario)
                        .map(Progreso::isDesafioFinalCompletado)
                        .orElse(false));

        return r;
    }
}