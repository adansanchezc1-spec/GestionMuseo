package com.model.model;
import com.model.enums.EstadoObra;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RestauradorJefe extends Usuario {

    private final List<Restauracion> restauraciones = new ArrayList<>();

    public RestauradorJefe(String nombre) {
        super(nombre, "RESTAURADOR_JEFE");
    }

    @Override
    public boolean autenticar(String contrasena) {
        return contrasena != null && contrasena.length() >= 6;
    }

    // ── Operaciones ───────────────────────────────────────────────────────────

    /**
     * Crea y registra una nueva restauración para la obra indicada.
     *
     * @param obra       obra a restaurar
     * @param fechaInicio fecha de inicio de la restauración
     * @return la restauración creada
     */
    public Restauracion iniciarRestauracion(Obra obra, LocalDate fechaInicio) {
        Restauracion r = new Restauracion(obra, fechaInicio);
        restauraciones.add(r);
        return r;
    }

    /**
     * Finaliza una restauración registrando su fecha de cierre.
     *
     * @param restauracion restauración activa a cerrar
     * @param fechaFin     fecha de finalización
     */
    public void finalizarRestauracion(Restauracion restauracion, LocalDate fechaFin) {
        restauracion.registrarFin(fechaFin);
    }

    /**
     * Devuelve todas las restauraciones ordenadas por antigüedad ascendente
     * (la más antigua primero).
     * GRASP Information Expert — opera sobre su propia colección.
     *
     * @return lista ordenada por fecha de inicio
     */
    public List<Restauracion> consultarRestauraciones() {
        return restauraciones.stream()
            .sorted(Comparator.comparing(Restauracion::getFechaInicio))
            .collect(Collectors.toList());
    }

    /**
     * Determina si una obra requiere restauración.
     * Reglas:
     *   - DAÑADA  → restauración inmediata.
     *   - Sin restauraciones previas y >= 5 años en el museo → necesita.
     *   - Última restauración finalizada hace >= 5 años → necesita.
     *
     * @param obra obra a evaluar
     * @return resultado con flag y motivo
     */
    public VerificacionResult verificarObraParaRestaurar(Obra obra) {
        if (obra.getEstado() == EstadoObra.DANIADA) {
            return new VerificacionResult(true,
                "La obra está DAÑADA — se requiere restauración inmediata.");
        }

        int aniosEnMuseo = LocalDate.now().getYear()
                         - obra.getFechaIngresoMuseo().getYear();

        List<Restauracion> porObra = restauraciones.stream()
            .filter(r -> r.getObra() == obra && !r.estaActiva())
            .sorted(Comparator.comparing(Restauracion::getFechaFin).reversed())
            .collect(Collectors.toList());

        if (porObra.isEmpty()) {
            boolean necesita = aniosEnMuseo >= Restauracion.INTERVALO_AUTOMATICO_ANIOS;
            return new VerificacionResult(necesita, necesita
                ? "Nunca restaurada y lleva " + aniosEnMuseo + " años en el museo."
                : "Con " + aniosEnMuseo + " años, aún no requiere restauración.");
        }

        int aniosDesdeFin = LocalDate.now().getYear()
                          - porObra.get(0).getFechaFin().getYear();
        boolean necesita = aniosDesdeFin >= Restauracion.INTERVALO_AUTOMATICO_ANIOS;
        return new VerificacionResult(necesita, necesita
            ? "Última restauración hace " + aniosDesdeFin + " años."
            : "Restaurada hace " + aniosDesdeFin + " años — sin intervención necesaria.");
    }

    @Override
    public List<String> mostrarMenu() {
        return Collections.unmodifiableList(Arrays.asList(
            "1. Iniciar restauración",
            "2. Finalizar restauración",
            "3. Ver restauraciones por antigüedad",
            "4. Verificar si una obra necesita restauración"
        ));
    }

    // ── Value Object resultado de verificación ────────────────────────────────

    public static final class VerificacionResult {
        private final boolean necesita;
        private final String  motivo;

        public VerificacionResult(boolean necesita, String motivo) {
            this.necesita = necesita;
            this.motivo   = motivo;
        }

        public boolean isNecesita() { return necesita; }
        public String  getMotivo()  { return motivo; }

        @Override
        public String toString() {
            return String.format("Necesita restauración: %b — %s", necesita, motivo);
        }
    }
}