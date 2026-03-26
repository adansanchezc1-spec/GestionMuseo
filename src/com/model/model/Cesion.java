package com.model.model;

import java.time.LocalDate;

public class Cesion {
    
    private final Obra              obra;
    private final MuseoColaborador  museo;
    private final double            importePagado;
    private final LocalDate         periodoInicio;
    private final LocalDate         periodoFin;
    private       boolean           finalizada;

    public Cesion(Obra obra, MuseoColaborador museo, double importePagado,
                  LocalDate periodoInicio, LocalDate periodoFin) {
        if (obra  == null) throw new IllegalArgumentException("La obra no puede ser null.");
        if (museo == null) throw new IllegalArgumentException("El museo no puede ser null.");
        if (periodoFin.isBefore(periodoInicio))
            throw new IllegalArgumentException("periodoFin no puede ser anterior a periodoInicio.");

        this.obra          = obra;
        this.museo         = museo;
        this.importePagado = importePagado;
        this.periodoInicio = periodoInicio;
        this.periodoFin    = periodoFin;
        this.finalizada    = false;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Obra             getObra()          { return obra; }
    public MuseoColaborador getMuseo()         { return museo; }
    public double           getImportePagado() { return importePagado; }
    public LocalDate        getPeriodoInicio() { return periodoInicio; }
    public LocalDate        getPeriodoFin()    { return periodoFin; }
    public boolean          isFinalizada()     { return finalizada; }

    // ── Comportamiento ────────────────────────────────────────────────────────

    /**
     * GRASP Information Expert — la cesión sabe si está vigente con sus propias fechas.
     *
     * @param fecha fecha a evaluar (normalmente LocalDate.now())
     * @return true si la cesión está activa en esa fecha
     */
    public boolean estaVigente(LocalDate fecha) {
        return !finalizada
            && !fecha.isBefore(periodoInicio)
            && !fecha.isAfter(periodoFin);
    }

    public boolean estaVigente() {
        return estaVigente(LocalDate.now());
    }

    /**
     * Finaliza la cesión antes del vencimiento pactado.
     *
     * @throws IllegalStateException si ya fue finalizada
     */
    public void finalizarCesion() {
        if (finalizada) throw new IllegalStateException("La cesión ya fue finalizada.");
        this.finalizada = true;
    }

    @Override
    public String toString() {
        return String.format(
            "Cesion{obra='%s', museo='%s', importe=%.2f, vigente=%b}",
            obra.getNombre(), museo.getNombre(), importePagado, estaVigente());
    }
}
