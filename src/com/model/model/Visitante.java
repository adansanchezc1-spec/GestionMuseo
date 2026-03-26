package com.model.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Visitante — Usuario de solo lectura que consulta obras por sala.
 *
 * SOLID LSP  : substituye a Usuario sin romper el sistema.
 * GRASP Information Expert : delega el filtrado al catálogo.
 */
public class Visitante extends Usuario {

    public Visitante(String nombre) {
        super(nombre, "VISITANTE");
    }

    @Override
    public boolean autenticar(String contrasena) {
        return contrasena != null && !contrasena.isEmpty();
    }

    /**
     * Devuelve las obras EXPUESTAS en una sala específica.
     *
     * @param catalogo   catálogo del museo
     * @param nombreSala sala a consultar
     * @return obras expuestas en esa sala
     */
    public List<Obra> verObras(Catalogo catalogo, String nombreSala) {
        return catalogo.listarPorSala(nombreSala).stream()
            .filter(o -> o.getEstado() == EstadoObra.EXPUESTA)
            .collect(Collectors.toList());
    }

    @Override
    public List<String> mostrarMenu() {
        return Collections.singletonList("1. Ver obras por sala");
    }
}

