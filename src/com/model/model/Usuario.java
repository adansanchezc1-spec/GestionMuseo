package com.model.model;

import java.util.List;

public abstract class Usuario {
    
    private final String nombre;
    private final String rol;

    protected Usuario(String nombre, String rol) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío.");
        this.nombre = nombre;
        this.rol    = rol;
    }

    public String getNombre() { return nombre; }
    public String getRol()    { return rol; }

    /**
     * Verifica las credenciales del usuario.
     * GRASP Protected Variations: cada subclase puede cambiar la política
     * de autenticación sin afectar a quien llama.
     *
     * @param contrasena credencial a verificar
     * @return true si la autenticación es correcta
     */
    public abstract boolean autenticar(String contrasena);

    public boolean verificarRol(String rolRequerido) {
        return this.rol.equals(rolRequerido);
    }

    /**
     * GOF Strategy hook — cada subclase entrega su propio menú de opciones.
     *
     * @return lista de opciones disponibles para este rol
     */
    public abstract List<String> mostrarMenu();

    @Override
    public String toString() {
        return String.format("Usuario{nombre='%s', rol='%s'}", nombre, rol);
    }
}
