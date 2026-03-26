package com.model.model;

public class MuseoColaborador {
    
    private final String nombre;
    private final String pais;

    public MuseoColaborador(String nombre, String pais) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre del museo no puede estar vacío.");
        if (pais == null || pais.trim().isEmpty())
            throw new IllegalArgumentException("El país del museo no puede estar vacío.");
        this.nombre = nombre;
        this.pais   = pais;
    }

    public String getNombre() { return nombre; }
    public String getPais()   { return pais; }

    @Override
    public String toString() {
        return String.format("MuseoColaborador{nombre='%s', pais='%s'}", nombre, pais);
    }
}
