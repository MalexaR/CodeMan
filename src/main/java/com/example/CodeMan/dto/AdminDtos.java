package com.example.CodeMan.dto;

public class AdminDtos {

    public record EstadisticasAdmin(
        long totalUsuarios,
        long totalPatrones,
        long totalPreguntas,
        long totalPartidas
    ) {}
}
