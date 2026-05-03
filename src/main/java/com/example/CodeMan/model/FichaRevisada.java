package com.example.CodeMan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fichas_revisadas", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "usuario_id", "ficha_id" })
})
public class FichaRevisada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Usuario que revisó la ficha.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /*
     * Ficha revisada por el usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    @Column(nullable = false)
    private boolean revisada = true;

    @Column(name = "fecha_revision", nullable = false)
    private LocalDateTime fechaRevision;

    @PrePersist
    public void prePersist() {
        if (fechaRevision == null) {
            fechaRevision = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Ficha getFicha() {
        return ficha;
    }

    public boolean isRevisada() {
        return revisada;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setFicha(Ficha ficha) {
        this.ficha = ficha;
    }

    public void setRevisada(boolean revisada) {
        this.revisada = revisada;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }
}