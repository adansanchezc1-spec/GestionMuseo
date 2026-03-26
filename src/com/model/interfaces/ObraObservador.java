package com.model.interfaces;
import com.model.model.Obra;
import com.model.enums.EstadoObra;
/**
 * Interfaz para observar cambios de estado en una Obra.
 *
 * GOF Observer: define el método de actualización que los Observadores implementarán.
 */

public interface ObraObservador {
    /**
     * Invocado cuando una Obra cambia de estado.
     *
     * @param obra           la obra que cambió
     * @param estadoAnterior estado previo
     * @param estadoNuevo    estado actual
     */
    void actualizar(Obra obra, EstadoObra estadoAnterior, EstadoObra estadoNuevo);
}
