package com.model.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Restauracion — Registro de una restauración de obra de arte.
 *
 * SOLID SRP   : solo gestiona datos y reglas de una restauración.
 * GRASP Information Expert : calcula su propia antigüedad con sus propias fechas.
 */
public class Restauracion {

    public static final int INTERVALO_AUTOMATICO_ANIOS = 5;

    private final Obra      obra;
    private final LocalDate fechaInicio;
    private       LocalDate fechaFin;

    public Restauracion(Obra obra, LocalDate fechaInicio) {
        if (obra == null)        throw new IllegalArgumentException("La obra no puede ser null.");
        if (fechaInicio == null) throw new IllegalArgumentException("La fecha de inicio no puede ser null.");
        this.obra        = obra;
        this.fechaInicio = fechaInicio;
        this.fechaFin    = null;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Obra      getObra()        { return obra; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin()    { return fechaFin; }
    public boolean   estaActiva()     { return fechaFin == null; }

    // ── Comportamiento ────────────────────────────────────────────────────────

    /**
     * Registra la finalización de la restauración.
     *
     * @param fechaFin no puede ser anterior a fechaInicio
     * @throws IllegalStateException    si la restauración ya fue finalizada
     * @throws IllegalArgumentException si fechaFin es anterior a fechaInicio
     */
    public void registrarFin(LocalDate fechaFin) {
        if (!estaActiva()) {
            throw new IllegalStateException("Esta restauración ya fue finalizada.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException(
                "La fecha de fin no puede ser anterior a la de inicio.");
        }
        this.fechaFin = fechaFin;
    }

    /**
     * GRASP Information Expert — la restauración conoce sus propias fechas
     * y calcula la antigüedad en días desde el inicio.
     *
     * @return días transcurridos desde el inicio (hasta hoy si aún activa)
     */
    public long getAntiguedadDias() {
        LocalDate referencia = estaActiva() ? LocalDate.now() : fechaFin;
        return ChronoUnit.DAYS.between(fechaInicio, referencia);
    }

    @Override
    public String toString() {
        return String.format("Restauracion{obra='%s', inicio=%s, fin=%s, activa=%b}",
            obra.getNombre(), fechaInicio, fechaFin, estaActiva());
    }
}
