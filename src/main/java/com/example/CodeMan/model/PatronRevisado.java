package com.example.CodeMan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "patrones_revisados",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "patron_id"})
)
public class PatronRevisado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patron_id", nullable = false)
    private Patron patron;

    @Column(name = "fecha_revision", nullable = false)
    private LocalDateTime fechaRevision;

    @PrePersist
    public void prePersist() {
        if (fechaRevision == null) fechaRevision = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Patron getPatron() { return patron; }
    public void setPatron(Patron patron) { this.patron = patron; }

    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }
}
