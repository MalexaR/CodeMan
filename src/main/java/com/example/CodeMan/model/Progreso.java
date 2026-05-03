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

    @Column(name = "fichas_revisadas", nullable = false)
    private int fichasRevisadas = 0;

    @Column(name = "partidas_jugadas", nullable = false)
    private int partidasJugadas = 0;

    @Column(nullable = false)
    private int aciertos = 0;

    @Column(nullable = false)
    private int errores = 0;

    @Column(name = "desafio_final_completado", nullable = false)
    private boolean desafioFinalCompletado = false;

    @Column(name = "ultimo_acceso", nullable = false)
    private LocalDateTime ultimoAcceso;

    @PrePersist
    public void prePersist() {
        if (ultimoAcceso == null)
            ultimoAcceso = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getFichasRevisadas() {
        return fichasRevisadas;
    }

    public void setFichasRevisadas(int fichasRevisadas) {
        this.fichasRevisadas = fichasRevisadas;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public int getAciertos() {
        return aciertos;
    }

    public void setAciertos(int aciertos) {
        this.aciertos = aciertos;
    }

    public int getErrores() {
        return errores;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public boolean isDesafioFinalCompletado() {
        return desafioFinalCompletado;
    }

    public void setDesafioFinalCompletado(boolean desafioFinalCompletado) {
        this.desafioFinalCompletado = desafioFinalCompletado;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}
