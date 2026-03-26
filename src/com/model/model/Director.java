package com.model.model;

import com.model.enums.EstadoObra;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Director extends Usuario {

    private final List<MuseoColaborador> museosColaboradores = new ArrayList<>();
    private final List<Cesion>           cesiones            = new ArrayList<>();

    public Director(String nombre) {
        super(nombre, "DIRECTOR");
    }

    @Override
    public boolean autenticar(String contrasena) {
        return contrasena != null && contrasena.length() >= 8;
    }

    // ── Operaciones ───────────────────────────────────────────────────────────

    /**
     * Registra un museo colaborador.
     *
     * @throws IllegalArgumentException si ya está registrado
     */
    public void gestionarMuseos(MuseoColaborador museo) {
        boolean existe = museosColaboradores.stream()
            .anyMatch(m -> m.getNombre().equalsIgnoreCase(museo.getNombre()));
        if (existe) {
            throw new IllegalArgumentException(
                "El museo \"" + museo.getNombre() + "\" ya está registrado.");
        }
        museosColaboradores.add(museo);
    }

    public List<MuseoColaborador> getMuseosColaboradores() {
        return Collections.unmodifiableList(museosColaboradores);
    }

    /**
     * Cede una obra a un museo colaborador y registra la cesión.
     *
     * @param obra         obra a ceder
     * @param museo        museo destinatario
     * @param importe      importe pactado
     * @param inicio       inicio del período de cesión
     * @param fin          fin del período de cesión
     * @return la cesión creada
     */
    public Cesion cederObra(Obra obra, MuseoColaborador museo,
                            double importe, LocalDate inicio, LocalDate fin) {
        Cesion cesion = new Cesion(obra, museo, importe, inicio, fin);
        obra.asignarEstado(EstadoObra.CEDIDA);
        cesiones.add(cesion);
        return cesion;
    }

    /**
     * Consulta la valoración total del catálogo.
     *
     * @param catalogo catálogo del museo
     * @return suma de valores económicos de todas las obras
     */
    public double consultarValoracionTotal(Catalogo catalogo) {
        return catalogo.valoracionTotal();
    }

    @Override
    public List<String> mostrarMenu() {
        return Collections.unmodifiableList(Arrays.asList(
            "1. Gestionar museos colaboradores",
            "2. Ceder obra a museo",
            "3. Consultar valoración total del catálogo"
        ));
    }
}
