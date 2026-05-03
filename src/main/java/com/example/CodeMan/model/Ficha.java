package com.example.CodeMan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fichas", uniqueConstraints = {
        @UniqueConstraint(columnNames = "patron_id")
})
public class Ficha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Una ficha pertenece a un patrón.
     * Se usa unique = true porque normalmente cada patrón tiene una ficha
     * principal.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patron_id", nullable = false, unique = true)
    private Patron patron;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "diagrama_uml_url", length = 500)
    private String diagramaUmlUrl;

    @Column(name = "ejemplo_codigo", nullable = false, columnDefinition = "TEXT")
    private String ejemploCodigo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creada_por_id")
    private Usuario creadaPor;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Patron getPatron() {
        return patron;
    }

    public String getContenido() {
        return contenido;
    }

    public String getDiagramaUmlUrl() {
        return diagramaUmlUrl;
    }

    public String getEjemploCodigo() {
        return ejemploCodigo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Usuario getCreadaPor() {
        return creadaPor;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPatron(Patron patron) {
        this.patron = patron;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setDiagramaUmlUrl(String diagramaUmlUrl) {
        this.diagramaUmlUrl = diagramaUmlUrl;
    }

    public void setEjemploCodigo(String ejemploCodigo) {
        this.ejemploCodigo = ejemploCodigo;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setCreadaPor(Usuario creadaPor) {
        this.creadaPor = creadaPor;
    }
}