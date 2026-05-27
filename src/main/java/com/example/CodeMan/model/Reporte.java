package com.example.CodeMan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_desafio_id", nullable = false)
    private Partida partidaDesafio;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "url_pdf", length = 500)
    private String urlPdf;

    @Column(name = "porcentaje_global", nullable = false)
    private double porcentajeGlobal;

    @Column(name = "datos_resumen", columnDefinition = "jsonb")
    private String datosResumen;

    @PrePersist
    public void prePersist() {
        if (fechaGeneracion == null)
            fechaGeneracion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Partida getPartidaDesafio() { return partidaDesafio; }
    public void setPartidaDesafio(Partida partidaDesafio) { this.partidaDesafio = partidaDesafio; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public String getUrlPdf() { return urlPdf; }
    public void setUrlPdf(String urlPdf) { this.urlPdf = urlPdf; }

    public double getPorcentajeGlobal() { return porcentajeGlobal; }
    public void setPorcentajeGlobal(double porcentajeGlobal) { this.porcentajeGlobal = porcentajeGlobal; }

    public String getDatosResumen() { return datosResumen; }
    public void setDatosResumen(String datosResumen) { this.datosResumen = datosResumen; }
}
