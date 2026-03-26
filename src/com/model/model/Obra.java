package com.model.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public abstract class Obra {

    // ── Atributos ─────────────────────────────────────────────────────────────
    private final String     nombre;
    private final String     autor;
    private final String     periodo;
    private final double     valorEconomico;
    private final LocalDate  fechaCreacion;
    private final LocalDate  fechaIngresoMuseo;
    private       EstadoObra estado;
    private       String     sala;

    // GOF Observer – lista de suscriptores
    private final List<ObraObservador> observadores = new ArrayList<>();

    // ── Constructor ──────────────────────────────────────────────────────────
    protected Obra(String nombre, String autor, String periodo,
                   double valorEconomico, LocalDate fechaCreacion,
                   LocalDate fechaIngresoMuseo, String sala) {
        this.nombre            = nombre;
        this.autor             = autor;
        this.periodo           = periodo;
        this.valorEconomico    = valorEconomico;
        this.fechaCreacion     = fechaCreacion;
        this.fechaIngresoMuseo = fechaIngresoMuseo;
        this.sala              = sala;
        this.estado            = EstadoObra.EXPUESTA;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String     getNombre()            { return nombre; }
    public String     getAutor()             { return autor; }
    public String     getPeriodo()           { return periodo; }
    public double     getValorEconomico()    { return valorEconomico; }
    public LocalDate  getFechaCreacion()     { return fechaCreacion; }
    public LocalDate  getFechaIngresoMuseo() { return fechaIngresoMuseo; }
    public EstadoObra getEstado()            { return estado; }
    public String     getSala()              { return sala; }

    // ── Comportamiento ────────────────────────────────────────────────────────

    /**
     * Cambia el estado de la obra y notifica a todos los observadores.
     * GOF Observer — cualquier cambio dispara la cadena de notificación.
     *
     * @param nuevoEstado estado destino (no puede ser null)
     * @throws IllegalArgumentException si nuevoEstado es null
     */
    public void asignarEstado(EstadoObra nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser null.");
        }
        EstadoObra anterior = this.estado;
        this.estado = nuevoEstado;

        if (nuevoEstado == EstadoObra.CEDIDA) {
            this.sala = null; // obra cedida no pertenece a ninguna sala del museo
        }

        notificarObservadores(anterior, nuevoEstado);
    }

    /**
     * GOF Template Method — esqueleto fijo de la descripción.
     * Las subclases aportan detalle a través de getDetalleEspecifico().
     */
    public final String getDescripcion() {
        return String.format(
            "Obra : %s%n  Autor  : %s%n  Periodo: %s%n  Estado : %s%n  Sala   : %s%n  Detalle: %s",
            nombre, autor, periodo, estado,
            sala != null ? sala : "Sin sala asignada",
            getDetalleEspecifico()
        );
    }

    /**
     * Hook del Template Method — cada subclase DEBE implementarlo.
     *
     * @return cadena con los atributos específicos del tipo de obra
     */
    protected abstract String getDetalleEspecifico();

    // ── GOF Observer ─────────────────────────────────────────────────────────

    public void suscribir(ObraObservador observador) {
        if (observador == null) throw new IllegalArgumentException("El observador no puede ser null.");
        observadores.add(observador);
    }

    public void desuscribir(ObraObservador observador) {
        observadores.remove(observador);
    }

    private void notificarObservadores(EstadoObra anterior, EstadoObra nuevo) {
        for (ObraObservador o : observadores) {
            o.actualizar(this, anterior, nuevo);
        }
    }

    // ── toString ──────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format("Obra{nombre='%s', estado=%s}", nombre, estado);
    }
}
