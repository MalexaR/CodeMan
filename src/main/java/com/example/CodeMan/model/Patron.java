package com.example.CodeMan.model;

import jakarta.persistence.*;

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

    @Column(columnDefinition = "TEXT")
    private String problema;

    @Column(name = "cuando_usar", columnDefinition = "TEXT")
    private String cuandoUsar;

    @Column(columnDefinition = "TEXT")
    private String estructura;

    @Column(columnDefinition = "TEXT")
    private String uml;

    @Column(name = "codigo_ejemplo", columnDefinition = "TEXT")
    private String codigoEjemplo;

    @Column(name = "problema_resuelto", columnDefinition = "TEXT")
    private String problemaResuelto;

    @Column(name = "cuando_usarlo", columnDefinition = "TEXT")
    private String cuandoUsarlo;

    @Column(name = "codigo_ejemplo_java", columnDefinition = "TEXT")
    private String codigoEjemploJava;

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getProposito() {
        return proposito;
    }

    public String getProblema() {
        return problema;
    }

    public String getCuandoUsar() {
        return cuandoUsar;
    }

    public String getEstructura() {
        return estructura;
    }

    public String getUml() {
        return uml;
    }

    public String getCodigoEjemplo() {
        return codigoEjemplo;
    }

    public String getProblemaResuelto() {
        return problemaResuelto;
    }

    public String getCuandoUsarlo() {
        return cuandoUsarlo;
    }

    public String getCodigoEjemploJava() {
        return codigoEjemploJava;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setProposito(String proposito) {
        this.proposito = proposito;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }

    public void setCuandoUsar(String cuandoUsar) {
        this.cuandoUsar = cuandoUsar;
    }

    public void setEstructura(String estructura) {
        this.estructura = estructura;
    }

    public void setUml(String uml) {
        this.uml = uml;
    }

    public void setCodigoEjemplo(String codigoEjemplo) {
        this.codigoEjemplo = codigoEjemplo;
    }

    public void setProblemaResuelto(String problemaResuelto) {
        this.problemaResuelto = problemaResuelto;
    }

    public void setCuandoUsarlo(String cuandoUsarlo) {
        this.cuandoUsarlo = cuandoUsarlo;
    }

    public void setCodigoEjemploJava(String codigoEjemploJava) {
        this.codigoEjemploJava = codigoEjemploJava;
    }
}