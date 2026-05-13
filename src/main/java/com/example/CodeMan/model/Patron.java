package com.example.CodeMan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patrones")
public class Patron {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Categoria categoria;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String proposito;

    @Column(name = "problema_resuelto", columnDefinition = "TEXT")
    private String problemaResuelto;

    @Column(name = "cuando_usar", columnDefinition = "TEXT")
    private String cuandoUsar;

    @Column(columnDefinition = "TEXT")
    private String estructura;

    @Column(name = "diagrama_uml_url", length = 500)
    private String diagramaUmlUrl;

    @Column(name = "codigo_ejemplo", columnDefinition = "TEXT")
    private String codigoEjemplo;

    @Column(columnDefinition = "TEXT")
    private String referencia;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id")
    private Usuario creadoPor;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getProposito() { return proposito; }
    public void setProposito(String proposito) { this.proposito = proposito; }

    public String getProblemaResuelto() { return problemaResuelto; }
    public void setProblemaResuelto(String problemaResuelto) { this.problemaResuelto = problemaResuelto; }

    public String getCuandoUsar() { return cuandoUsar; }
    public void setCuandoUsar(String cuandoUsar) { this.cuandoUsar = cuandoUsar; }

    public String getEstructura() { return estructura; }
    public void setEstructura(String estructura) { this.estructura = estructura; }

    public String getDiagramaUmlUrl() { return diagramaUmlUrl; }
    public void setDiagramaUmlUrl(String diagramaUmlUrl) { this.diagramaUmlUrl = diagramaUmlUrl; }

    public String getCodigoEjemplo() { return codigoEjemplo; }
    public void setCodigoEjemplo(String codigoEjemplo) { this.codigoEjemplo = codigoEjemplo; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Usuario getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Usuario creadoPor) { this.creadoPor = creadoPor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
