package com.example.CodeMan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progreso")
public class Progreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Transient
    private int patronesRevisadosCache = 0;

    @Transient
    private int partidasJugadasCache = 0;

    @Transient
    private int aciertosCache = 0;

    @Transient
    private int erroresCache = 0;

    @Column(name = "desafio_final_completado", nullable = false)
    private boolean desafioFinalCompletado = false;

    @Column(name = "ultimo_acceso", nullable = false)
    private LocalDateTime ultimoAcceso;

    @Transient
    private long totalPatrones;

    @PrePersist
    public void prePersist() {
        if (ultimoAcceso == null) ultimoAcceso = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public int getPatronesRevisadosCache() { return patronesRevisadosCache; }
    public void setPatronesRevisadosCache(int patronesRevisadosCache) { this.patronesRevisadosCache = patronesRevisadosCache; }

    public int getPartidasJugadasCache() { return partidasJugadasCache; }
    public void setPartidasJugadasCache(int partidasJugadasCache) { this.partidasJugadasCache = partidasJugadasCache; }

    public int getAciertosCache() { return aciertosCache; }
    public void setAciertosCache(int aciertosCache) { this.aciertosCache = aciertosCache; }

    public int getErroresCache() { return erroresCache; }
    public void setErroresCache(int erroresCache) { this.erroresCache = erroresCache; }

    public boolean isDesafioFinalCompletado() { return desafioFinalCompletado; }
    public void setDesafioFinalCompletado(boolean desafioFinalCompletado) { this.desafioFinalCompletado = desafioFinalCompletado; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public long getTotalPatrones() { return totalPatrones; }
    public void setTotalPatrones(long totalPatrones) { this.totalPatrones = totalPatrones; }

    public int getPorcentajePatrones() {
        return totalPatrones == 0 ? 0 : (int) Math.round((patronesRevisadosCache * 100.0) / totalPatrones);
    }

    public int getPorcentajeAciertos() {
        long total = aciertosCache + erroresCache;
        return total == 0 ? 0 : (int) Math.round((aciertosCache * 100.0) / total);
    }
}
