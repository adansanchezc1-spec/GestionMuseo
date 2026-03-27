package com.model.interfaces;

import com.model.model.Obra;
import java.util.List;
/**
 * Interfaz Sala — Representa una sala del museo que contiene obras.
 *
 * GRASP Information Expert : la sala conoce sus obras, por lo que es responsable de devolverlas.
 */

public interface Sala {
        String getNombre();
    List<Obra> getObras();
}
