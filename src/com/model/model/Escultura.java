package com.model.model;

import java.time.LocalDate;

public class Escultura extends Obra {
     private final String material;

    public Escultura(String nombre, String autor, String periodo,
                     double valorEconomico, LocalDate fechaCreacion,
                     LocalDate fechaIngresoMuseo, String sala,
                     String material) {
        super(nombre, autor, periodo, valorEconomico,
              fechaCreacion, fechaIngresoMuseo, sala);
        this.material = material;
    }

    public String getMaterial() { return material; }

    @Override
    protected String getDetalleEspecifico() {
        return "Material: " + material;
    } 
}
