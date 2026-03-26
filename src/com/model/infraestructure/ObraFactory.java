package com.model.infraestructure;

import java.time.LocalDate;

import com.model.model.*;

/**
 * ObraFactory — Crea instancias de obras sin exponer las clases concretas al cliente.
 *
 * GOF  Factory Method : el cliente solicita un tipo; la fábrica decide
 *                       qué clase concreta instanciar.
 * SOLID DIP : los servicios dependen de esta fábrica (abstracción),
 *             no de Cuadro / Escultura / OtroObjeto directamente.
 * SOLID OCP : agregar un nuevo tipo de obra = agregar un nuevo case aquí,
 *             sin tocar el código cliente.
 */
public class ObraFactory {

    /** Tipos de obra reconocidos por la fábrica. */
    public enum TipoObra {
        CUADRO,
        ESCULTURA,
        OTRO_OBJETO
    }

    private ObraFactory() {
        // Clase utilitaria — no instanciable
    }

    /**
     * Crea una obra del tipo indicado.
     *
     * @param tipo   tipo de obra a crear (no {@code null})
     * @param params parámetros de construcción (no {@code null})
     * @return instancia concreta de {@link Obra}
     * @throws IllegalArgumentException si {@code tipo} o {@code params} son {@code null},
     *         faltan campos comunes, el valor económico no es válido o faltan datos del tipo
     */
    public static Obra crear(TipoObra tipo, ObraParams params) {
        if (tipo == null) {
            throw new IllegalArgumentException("tipo es obligatorio.");
        }
        if (params == null) {
            throw new IllegalArgumentException("params es obligatorio.");
        }
        validarComunales(params);
        String nombre = params.getNombre().trim();
        String autor = params.getAutor().trim();
        String periodo = params.getPeriodo().trim();
        String sala = params.getSala().trim();
        double valor = params.getValorEconomico();
        LocalDate fechaCreacion = params.getFechaCreacion();
        LocalDate fechaIngreso = params.getFechaIngresoMuseo();

        Obra obra = null;
        switch (tipo) {
            case CUADRO:
                if (esCadenaVacia(params.getTecnica())) {
                    throw new IllegalArgumentException(
                        "Cuadro requiere 'tecnica' no vacía.");
                }
                if (esCadenaVacia(params.getEstilo())) {
                    throw new IllegalArgumentException(
                        "Cuadro requiere 'estilo' no vacío.");
                }
                obra = new Cuadro(
                    nombre, autor, periodo, valor, fechaCreacion, fechaIngreso, sala,
                    params.getTecnica().trim(), params.getEstilo().trim()
                );
                break;
            case ESCULTURA:
                if (esCadenaVacia(params.getMaterial())) {
                    throw new IllegalArgumentException(
                        "Escultura requiere 'material' no vacío.");
                }
                obra = new Escultura(
                    nombre, autor, periodo, valor, fechaCreacion, fechaIngreso, sala,
                    params.getMaterial().trim()
                );
                break;
            case OTRO_OBJETO:
                obra = new OtroObjeto(
                    nombre, autor, periodo, valor, fechaCreacion, fechaIngreso, sala
                );
                break;
        }
        if (obra == null) {
            throw new IllegalStateException("Tipo no contemplado: " + tipo);
        }
        return obra;
    }

    private static boolean esCadenaVacia(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static void validarComunales(ObraParams p) {
        if (esCadenaVacia(p.getNombre())) {
            throw new IllegalArgumentException("nombre es obligatorio.");
        }
        if (esCadenaVacia(p.getAutor())) {
            throw new IllegalArgumentException("autor es obligatorio.");
        }
        if (esCadenaVacia(p.getPeriodo())) {
            throw new IllegalArgumentException("periodo es obligatorio.");
        }
        if (p.getFechaCreacion() == null) {
            throw new IllegalArgumentException("fechaCreacion es obligatoria.");
        }
        if (p.getFechaIngresoMuseo() == null) {
            throw new IllegalArgumentException("fechaIngresoMuseo es obligatoria.");
        }
        if (esCadenaVacia(p.getSala())) {
            throw new IllegalArgumentException("sala es obligatoria.");
        }
        double valor = p.getValorEconomico();
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            throw new IllegalArgumentException("valorEconomico debe ser un número finito.");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("valorEconomico no puede ser negativo.");
        }
    }

    // ── Value Object de parámetros (Builder interno) ──────────────────────────

    /**
     * ObraParams — Objeto inmutable de parámetros para ObraFactory.
     * Usa el patrón Builder para evitar constructores con demasiados argumentos
     * (Clean Code: no más de 3 argumentos en un constructor).
     *
     * <p>Campos no asignados en el builder: {@code valorEconomico} será {@code 0.0};
     * el resto queda en {@code null} hasta que se configure.</p>
     */
    public static final class ObraParams {

        private final String    nombre;
        private final String    autor;
        private final String    periodo;
        private final double    valorEconomico;
        private final LocalDate fechaCreacion;
        private final LocalDate fechaIngresoMuseo;
        private final String    sala;
        private final String    tecnica;
        private final String    estilo;
        private final String    material;

        private ObraParams(Builder b) {
            this.nombre            = b.nombre;
            this.autor             = b.autor;
            this.periodo           = b.periodo;
            this.valorEconomico    = b.valorEconomico;
            this.fechaCreacion     = b.fechaCreacion;
            this.fechaIngresoMuseo = b.fechaIngresoMuseo;
            this.sala              = b.sala;
            this.tecnica           = b.tecnica;
            this.estilo            = b.estilo;
            this.material          = b.material;
        }

        public String    getNombre()            { return nombre; }
        public String    getAutor()             { return autor; }
        public String    getPeriodo()           { return periodo; }
        public double    getValorEconomico()    { return valorEconomico; }
        public LocalDate getFechaCreacion()     { return fechaCreacion; }
        public LocalDate getFechaIngresoMuseo() { return fechaIngresoMuseo; }
        public String    getSala()              { return sala; }
        public String    getTecnica()           { return tecnica; }
        public String    getEstilo()            { return estilo; }
        public String    getMaterial()          { return material; }

        // ── Builder ───────────────────────────────────────────────────────────

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private String    nombre;
            private String    autor;
            private String    periodo;
            private double    valorEconomico;
            private LocalDate fechaCreacion;
            private LocalDate fechaIngresoMuseo;
            private String    sala;
            private String    tecnica;
            private String    estilo;
            private String    material;

            public Builder nombre(String v)               { this.nombre            = v; return this; }
            public Builder autor(String v)                { this.autor             = v; return this; }
            public Builder periodo(String v)              { this.periodo           = v; return this; }
            public Builder valorEconomico(double v)         { this.valorEconomico    = v; return this; }
            public Builder fechaCreacion(LocalDate v)     { this.fechaCreacion     = v; return this; }
            public Builder fechaIngresoMuseo(LocalDate v) { this.fechaIngresoMuseo = v; return this; }
            public Builder sala(String v)                 { this.sala              = v; return this; }
            public Builder tecnica(String v)              { this.tecnica           = v; return this; }
            public Builder estilo(String v)               { this.estilo            = v; return this; }
            public Builder material(String v)             { this.material          = v; return this; }

            public ObraParams build() {
                return new ObraParams(this);
            }
        }
    }
}

