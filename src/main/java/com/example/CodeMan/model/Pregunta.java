package com.example.CodeMan.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El enunciado es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String enunciado;

    @NotBlank(message = "La opción A es obligatoria")
    @Column(name = "opcion_a", nullable = false, columnDefinition = "TEXT")
    private String opcionA;

    @NotBlank(message = "La opción B es obligatoria")
    @Column(name = "opcion_b", nullable = false, columnDefinition = "TEXT")
    private String opcionB;

    @NotBlank(message = "La opción C es obligatoria")
    @Column(name = "opcion_c", nullable = false, columnDefinition = "TEXT")
    private String opcionC;

    @NotBlank(message = "La opción D es obligatoria")
    @Column(name = "opcion_d", nullable = false, columnDefinition = "TEXT")
    private String opcionD;

    @Enumerated(EnumType.STRING)
    @Column(name = "respuesta_correcta", nullable = false, length = 10)
    private OpcionRespuesta respuestaCorrecta;

    @Column(columnDefinition = "TEXT")
    private String explicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patron_id")
    private Patron patron;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Dificultad dificultad = Dificultad.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 40)
    private TipoEvento tipoEvento = TipoEvento.PREGUNTA_NORMAL;

    @Column(nullable = false)
    private boolean activa = true;

    public Long getId() {
        return id;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public String getOpcionA() {
        return opcionA;
    }

    public String getOpcionB() {
        return opcionB;
    }

    public String getOpcionC() {
        return opcionC;
    }

    public String getOpcionD() {
        return opcionD;
    }

    public OpcionRespuesta getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Patron getPatron() {
        return patron;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public void setOpcionA(String opcionA) {
        this.opcionA = opcionA;
    }

    public void setOpcionB(String opcionB) {
        this.opcionB = opcionB;
    }

    public void setOpcionC(String opcionC) {
        this.opcionC = opcionC;
    }

    public void setOpcionD(String opcionD) {
        this.opcionD = opcionD;
    }

    public void setRespuestaCorrecta(OpcionRespuesta respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setPatron(Patron patron) {
        this.patron = patron;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
