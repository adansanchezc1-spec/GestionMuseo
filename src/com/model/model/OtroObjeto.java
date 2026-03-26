package com.model.model;

import java.time.LocalDate;

public class OtroObjeto extends Obra {
   
    public OtroObjeto(String nombre, String autor, String periodo,
                      double valorEconomico, LocalDate fechaCreacion,
                      LocalDate fechaIngresoMuseo, String sala) {
        super(nombre, autor, periodo, valorEconomico,
              fechaCreacion, fechaIngresoMuseo, sala);
    }

    @Override
    protected String getDetalleEspecifico() {
        return "Objeto genérico sin atributos adicionales.";
    }
    
}
