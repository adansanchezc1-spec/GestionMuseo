package com.model.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Catalogo {
     private static Catalogo instancia;

    public static synchronized Catalogo obtenerInstancia() {
        if (instancia == null) {
            instancia = new Catalogo();
        }
        return instancia;
    }

    /** Solo para tests — permite resetear el singleton entre pruebas. */
    public static synchronized void resetInstancia() {
        instancia = null;
    }
    // ── Estado interno ────────────────────────────────────────────────────────
    private final String     nombre     = "Catálogo Principal";
    private final List<Obra> listaObras = new ArrayList<>();

    private Catalogo() {}

    // ── Sala interface ────────────────────────────────────────────────────────
    @Override
    public String getNombre() { return nombre; }

    @Override
    public List<Obra> getObras() {
        return Collections.unmodifiableList(listaObras);
    }

    // ── Gestión de obras ─────────────────────────────────────────────────────

    /**
     * Agrega una obra al catálogo.
     *
     * @throws IllegalArgumentException si el nombre ya existe en el catálogo
     */
    public void agregarObra(Obra obra) {
        boolean existe = listaObras.stream()
            .anyMatch(o -> o.getNombre().equalsIgnoreCase(obra.getNombre()));
        if (existe) {
            throw new IllegalArgumentException(
                "Ya existe una obra con el nombre \"" + obra.getNombre() + "\".");
        }
        listaObras.add(obra);
    }

    /** @return vista inmutable de todas las obras */
    public List<Obra> listarObras() {
        return Collections.unmodifiableList(listaObras);
    }

    /**
     * Filtra obras por nombre de sala.
     *
     * @param nombreSala nombre exacto de la sala
     * @return lista de obras en esa sala
     */
    public List<Obra> listarPorSala(String nombreSala) {
        return listaObras.stream()
            .filter(o -> Objects.equals(nombreSala, o.getSala()))
            .collect(Collectors.toList());
    }

    /**
     * GRASP Information Expert — suma los valores económicos de todas las obras.
     *
     * @return valoración total del catálogo
     */
    public double valoracionTotal() {
        return listaObras.stream()
            .mapToDouble(Obra::getValorEconomico)
            .sum();
    }
}
