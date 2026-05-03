package com.example.CodeMan.model;

import jakarta.persistence.*;

@Entity
@Table(name = "respuesta_partida")
public class RespuestaPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    @Enumerated(EnumType.STRING)
    @Column(name = "opcion_elegida", length = 10)
    private OpcionRespuesta opcionElegida;

    @Column(name = "es_correcta", nullable = false)
    private boolean esCorrecta = false;

    @Column(name = "tiempo_respuesta")
    private Integer tiempoRespuesta;

    @Column(name = "tiempo_respuesta_ms")
    private Integer tiempoRespuestaMs;

    @Column(nullable = false)
    private boolean timeout = false;

    @Column(name = "orden_en_partida")
    private Integer ordenEnPartida;

    public Long getId() {
        return id;
    }

    public Partida getPartida() {
        return partida;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public OpcionRespuesta getOpcionElegida() {
        return opcionElegida;
    }

    public boolean isEsCorrecta() {
        return esCorrecta;
    }

    public Integer getTiempoRespuesta() {
        return tiempoRespuesta;
    }

    public Integer getTiempoRespuestaMs() {
        return tiempoRespuestaMs;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public Integer getOrdenEnPartida() {
        return ordenEnPartida;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public void setOpcionElegida(OpcionRespuesta opcionElegida) {
        this.opcionElegida = opcionElegida;
    }

    public void setEsCorrecta(boolean esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    public void setTiempoRespuesta(Integer tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }

    public void setTiempoRespuestaMs(Integer tiempoRespuestaMs) {
        this.tiempoRespuestaMs = tiempoRespuestaMs;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    public void setOrdenEnPartida(Integer ordenEnPartida) {
        this.ordenEnPartida = ordenEnPartida;
    }
}
