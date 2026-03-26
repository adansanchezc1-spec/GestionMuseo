package com.model.model;


import java.time.LocalDate;

public class Cuadro  extends Obra {
 
    private final String tecnica;
    private final String estilo;

    public Cuadro(String nombre, String autor, String periodo,
                  double valorEconomico, LocalDate fechaCreacion,
                  LocalDate fechaIngresoMuseo, String sala,
                  String tecnica, String estilo) {
        super(nombre, autor, periodo, valorEconomico,
              fechaCreacion, fechaIngresoMuseo, sala);
        this.tecnica = tecnica;
        this.estilo  = estilo;
    }

    public String getTecnica() { return tecnica; }
    public String getEstilo()  { return estilo; }

    @Override
    protected String getDetalleEspecifico() {
        return String.format("Técnica: %s | Estilo: %s", tecnica, estilo);
    }
}
