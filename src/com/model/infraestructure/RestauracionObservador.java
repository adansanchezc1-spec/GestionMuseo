
import museo.domain.enums.EstadoObra;
import museo.domain.interfaces.ObraObservador;
import museo.domain.model.Obra;
import museo.domain.model.Restauracion;
import museo.domain.model.RestauradorJefe;
import museo.infrastructure.repositories.Repositories.RestauracionRepository;

import java.time.LocalDate;

/**
 * RestauracionObservador — Reacciona a cambios de estado en obras.
 *
 * GOF  Observer : implementa ObraObservador; se suscribe a una Obra
 *                 y actúa cuando su estado cambia a DAÑADA.
 * SOLID SRP : única responsabilidad — detectar el estado DAÑADA y
 *             disparar la restauración de emergencia.
 * SOLID OCP : nuevos comportamientos ante cambios de estado =
 *             nuevos observadores, sin modificar éste.
 */
public class RestauracionObservador implements ObraObservador {

    private final RestauradorJefe       restauradorJefe;
    private final RestauracionRepository restauracionRepo;

    public RestauracionObservador(RestauradorJefe restauradorJefe,
                                  RestauracionRepository restauracionRepo) {
        if (restauradorJefe == null)
            throw new IllegalArgumentException("RestauradorJefe no puede ser null.");
        if (restauracionRepo == null)
            throw new IllegalArgumentException("RestauracionRepository no puede ser null.");
        this.restauradorJefe = restauradorJefe;
        this.restauracionRepo = restauracionRepo;
    }

    /**
     * Callback invocado por Obra al cambiar de estado.
     * Si el nuevo estado es DAÑADA (y no venía ya de EN_REPARACION),
     * inicia una restauración de emergencia automáticamente.
     */
    @Override
    public void actualizar(Obra obra, EstadoObra estadoAnterior, EstadoObra estadoNuevo) {
        if (estadoNuevo == EstadoObra.DANIADA
                && estadoAnterior != EstadoObra.EN_REPARACION) {

            System.out.printf("[RestauracionObservador] ⚠  Obra \"%s\" marcada como DAÑADA.%n",
                obra.getNombre());
            System.out.println("[RestauracionObservador]    Iniciando restauración de emergencia...");

            Restauracion r = restauradorJefe.iniciarRestauracion(obra, LocalDate.now());
            restauracionRepo.guardar(r);
            obra.asignarEstado(EstadoObra.EN_REPARACION);

            System.out.printf("[RestauracionObservador] ✔  Restauración iniciada el %s%n",
                r.getFechaInicio());
        }
    }
}
