package com.model.model;

import com.model.enums.EstadoObra;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EncargadoCatalogo extends Usuario {
      public EncargadoCatalogo(String nombre) {
        super(nombre, "ENCARGADO_CATALOGO");
    }

    @Override
    public boolean autenticar(String contrasena) {
        return contrasena != null && contrasena.length() >= 6;
    }

    /**
     * Inscribe una obra en el catálogo.
     *
     * @param obra     obra a inscribir
     * @param catalogo catálogo destino
     */
    public void inscribirObra(Obra obra, Catalogo catalogo) {
        catalogo.agregarObra(obra);
    }

    /**
     * Asigna un nuevo estado a una obra.
     *
     * @param obra        obra a modificar
     * @param nuevoEstado estado destino
     */
    public void asignarEstado(Obra obra, EstadoObra nuevoEstado) {
        obra.asignarEstado(nuevoEstado);
    }

    @Override
    public List<String> mostrarMenu() {
        return Collections.unmodifiableList(Arrays.asList(
            "1. Inscribir nueva obra",
            "2. Asignar estado a obra",
            "3. Listar obras del catálogo"
        ));
    }
}
