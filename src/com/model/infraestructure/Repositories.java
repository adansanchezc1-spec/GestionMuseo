package com.model.infraestructure;

import museo.domain.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repositorios en memoria para las entidades del dominio.
 *
 * GRASP Pure Fabrication : no corresponden a conceptos del dominio puro;
 *   existen para separar la persistencia del modelo.
 * SOLID SRP : cada repositorio gestiona un único tipo de entidad.
 * SOLID DIP : los servicios de aplicación dependen de estas clases
 *   (o sus interfaces en un sistema real con BD).
 */
public final class Repositories {

    private Repositories() {}

    // ─── RestauracionRepository ───────────────────────────────────────────────

    public static class RestauracionRepository {

        private final List<Restauracion> store = new ArrayList<>();

        public void guardar(Restauracion r) {
            store.add(r);
        }

        public List<Restauracion> findAll() {
            return Collections.unmodifiableList(store);
        }

        public List<Restauracion> findByObra(Obra obra) {
            return store.stream()
                .filter(r -> r.getObra() == obra)
                .collect(Collectors.toList());
        }

        public List<Restauracion> findActivas() {
            return store.stream()
                .filter(Restauracion::estaActiva)
                .collect(Collectors.toList());
        }

        /**
         * Todas las restauraciones ordenadas por fecha de inicio ascendente
         * (la más antigua primero).
         */
        public List<Restauracion> findTodasOrdenadasPorAntiguedad() {
            return store.stream()
                .sorted(Comparator.comparing(Restauracion::getFechaInicio))
                .collect(Collectors.toList());
        }
    }

    // ─── CesionRepository ─────────────────────────────────────────────────────

    public static class CesionRepository {

        private final List<Cesion> store = new ArrayList<>();

        public void guardar(Cesion c)            { store.add(c); }
        public List<Cesion> findAll()            { return Collections.unmodifiableList(store); }
        public List<Cesion> findVigentes()       { return store.stream().filter(Cesion::estaVigente).collect(Collectors.toList()); }
        public List<Cesion> findByObra(Obra obra){ return store.stream().filter(c -> c.getObra() == obra).collect(Collectors.toList()); }
    }

    // ─── MuseoColaboradorRepository ───────────────────────────────────────────

    public static class MuseoColaboradorRepository {

        private final List<MuseoColaborador> store = new ArrayList<>();

        public void guardar(MuseoColaborador m) {
            boolean existe = store.stream()
                .anyMatch(x -> x.getNombre().equalsIgnoreCase(m.getNombre()));
            if (existe) throw new IllegalArgumentException(
                "Museo \"" + m.getNombre() + "\" ya registrado.");
            store.add(m);
        }

        public List<MuseoColaborador> findAll() {
            return Collections.unmodifiableList(store);
        }

        public Optional<MuseoColaborador> findByNombre(String nombre) {
            return store.stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
        }
    }
}

