package com.example.CodeMan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partidas")
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_juego", nullable = false)
    private ModoJuego modoJuego = ModoJuego.PRACTICA;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "vidas_restantes", nullable = false)
    private int vidasRestantes = 3;

    @Column(nullable = false)
    private int puntuacion = 0;

    @Column(nullable = false)
    private boolean completada = false;

    @Column(name = "errores_acumulados", nullable = false)
    private int erroresAcumulados = 0;

    @Column(name = "fantasma_extra_activo", nullable = false)
    private boolean fantasmaExtraActivo = false;

    @PrePersist
    public void prePersist() {
        if (fechaInicio == null)
            fechaInicio = LocalDateTime.now();
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public ModoJuego getModoJuego() {
        return modoJuego;
    }

    public void setModoJuego(ModoJuego modoJuego) {
        this.modoJuego = modoJuego;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getVidasRestantes() {
        return vidasRestantes;
    }

    public void setVidasRestantes(int vidasRestantes) {
        this.vidasRestantes = vidasRestantes;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public int getErroresAcumulados() {
        return erroresAcumulados;
    }

    public void setErroresAcumulados(int erroresAcumulados) {
        this.erroresAcumulados = erroresAcumulados;
    }

    public boolean isFantasmaExtraActivo() {
        return fantasmaExtraActivo;
    }

    public void setFantasmaExtraActivo(boolean fantasmaExtraActivo) {
        this.fantasmaExtraActivo = fantasmaExtraActivo;
    }
}
