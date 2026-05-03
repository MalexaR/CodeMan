package com.example.CodeMan.service;

public class ProgresoResumen {
    private long fichasRevisadas;
    private long totalFichas;
    private long partidasJugadas;
    private long aciertos;
    private long errores;
    private boolean desafioFinalCompletado;

    public long getFichasRevisadas() {
        return fichasRevisadas;
    }

    public void setFichasRevisadas(long fichasRevisadas) {
        this.fichasRevisadas = fichasRevisadas;
    }

    public long getTotalFichas() {
        return totalFichas;
    }

    public void setTotalFichas(long totalFichas) {
        this.totalFichas = totalFichas;
    }

    public long getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(long partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public long getAciertos() {
        return aciertos;
    }

    public void setAciertos(long aciertos) {
        this.aciertos = aciertos;
    }

    public long getErrores() {
        return errores;
    }

    public void setErrores(long errores) {
        this.errores = errores;
    }

    public boolean isDesafioFinalCompletado() {
        return desafioFinalCompletado;
    }

    public void setDesafioFinalCompletado(boolean desafioFinalCompletado) {
        this.desafioFinalCompletado = desafioFinalCompletado;
    }

    public int getPorcentajeFichas() {
        return totalFichas == 0 ? 0 : (int) Math.round((fichasRevisadas * 100.0) / totalFichas);
    }

    public int getPorcentajeAciertos() {
        long total = aciertos + errores;
        return total == 0 ? 0 : (int) Math.round((aciertos * 100.0) / total);
    }
}
