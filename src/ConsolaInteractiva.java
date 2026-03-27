import com.model.enums.EstadoObra;
import com.model.infraestructure.ObraFactory;
import com.model.model.Catalogo;
import com.model.model.Cesion;
import com.model.model.Director;
import com.model.model.EncargadoCatalogo;
import com.model.model.Obra;
import com.model.model.Restauracion;
import com.model.model.RestauradorJefe;
import com.model.model.Usuario;
import com.model.model.Visitante;
import com.model.service.Services.AutenticacionService;
import com.model.service.Services.CatalogoService;
import com.model.service.Services.CesionService;
import com.model.service.Services.RestauracionService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsolaInteractiva {

    private final Catalogo             catalogo;
    private final EncargadoCatalogo    encargado;
    private final RestauradorJefe      restaurador;
    private final Visitante            visitante;
    private final Director             director;
    private final CatalogoService      catalogoSvc;
    private final RestauracionService  restauracionSvc;
    private final CesionService        cesionSvc;
    private final AutenticacionService authSvc;

    private final Scanner scanner = new Scanner(System.in);

    public ConsolaInteractiva(
        Catalogo catalogo,
        EncargadoCatalogo encargado,
        RestauradorJefe restaurador,
        Visitante visitante,
        Director director,
        CatalogoService catalogoSvc,
        RestauracionService restauracionSvc,
        CesionService cesionSvc,
        AutenticacionService authSvc
    ) {
        this.catalogo = catalogo;
        this.encargado = encargado;
        this.restaurador = restaurador;
        this.visitante = visitante;
        this.director = director;
        this.catalogoSvc = catalogoSvc;
        this.restauracionSvc = restauracionSvc;
        this.cesionSvc = cesionSvc;
        this.authSvc = authSvc;
    }

    public void ejecutar() {
        println("=== MUSEO - MODO INTERACTIVO ===");

        while (true) {
            Usuario usuario = iniciarSesion();

            while (true) {
                println("\nRol: " + usuario.getRol());
                println("--- Menú ---");
                List<String> opciones = usuario.mostrarMenu();
                for (int i = 0; i < opciones.size(); i++) {
                    println((i + 1) + ". " + opciones.get(i));
                }
                println("0. Salir al menú principal");

                int opcion = leerEntero("Seleccione una opción", 0, opciones.size());
                if (opcion == 0) {
                    break;
                }

                try {
                    ejecutarOpcion(usuario, opcion);
                } catch (Exception ex) {
                    println("Error: " + ex.getMessage());
                }
            }
        }
    }

    private Usuario iniciarSesion() {
        List<Usuario> usuarios = Arrays.asList(encargado, restaurador, visitante, director);
        while (true) {
            println("\nSeleccione un rol para iniciar sesión:");
            for (int i = 0; i < usuarios.size(); i++) {
                Usuario u = usuarios.get(i);
                println((i + 1) + ". " + u.getRol());
            }

            int seleccion = leerEntero("Seleccione rol", 1, usuarios.size());
            Usuario usuario = usuarios.get(seleccion - 1);

            String clave = leerLinea("Ingrese contraseña");
            try {
                authSvc.autenticar(usuario, clave);
                println("Autenticación exitosa. Rol: " + usuario.getRol());
                return usuario;
            } catch (SecurityException se) {
                println("Autenticación inválida: " + se.getMessage());
            }
        }
    }

    private void ejecutarOpcion(Usuario usuario, int opcion) {
        switch (usuario) {
            case EncargadoCatalogo encargadoCatalogo -> ejecutarEncargado(encargadoCatalogo, opcion);
            case RestauradorJefe restauradorJefe -> ejecutarRestaurador(restauradorJefe, opcion);
            case Visitante visitante1 -> ejecutarVisitante(visitante1, opcion);
            case Director director1 -> ejecutarDirector(director1, opcion);
            default -> throw new IllegalStateException("Tipo de usuario no manejado: " + usuario.getRol());
        }
    }

    private void ejecutarEncargado(EncargadoCatalogo encargadoCatalogo, int opcion) {
        switch (opcion) {
            case 1:
                inscribirObra();
                break;
            case 2:
                asignarEstadoObra();
                break;
            case 3:
                listarObras();
                break;
            default:
                println("Opción inválida para Encargado.");
        }
    }

    private void ejecutarRestaurador(RestauradorJefe usuario, int opcion) {
        switch (opcion) {
            case 1 -> iniciarRestauracion();
            case 2 -> finalizarRestauracion();
            case 3 -> listarRestauraciones();
            case 4 -> verificarRestauracion();
            default -> println("Opción inválida para Restaurador.");
        }
    }

    private void ejecutarVisitante(Visitante usuario, int opcion) {
        switch (opcion) {
            case 1 -> consultarObrasPorSala();
            default -> println("Opción inválida para Visitante.");
        }
    }

    private void ejecutarDirector(Director usuario, int opcion) {
        switch (opcion) {
            case 1 -> registrarMuseo();
            case 2 -> cederObra();
            case 3 -> consultarValoracionTotal();
            default -> println("Opción inválida para Director.");
        }
    }

    private void inscribirObra() {
        println("--- Inscribir nueva obra ---");
        println("Tipo de obra: 1=Cuadro 2=Escultura 3=Otro objeto");
        int t = leerEntero("Seleccione el tipo", 1, 3);
        ObraFactory.TipoObra tipo = t == 1 ? ObraFactory.TipoObra.CUADRO
                            : t == 2 ? ObraFactory.TipoObra.ESCULTURA
                            : ObraFactory.TipoObra.OTRO_OBJETO;

        String nombre = leerLinea("Nombre");
        String autor = leerLinea("Autor");
        String periodo = leerLinea("Periodo");
        double valor = leerDouble("Valor económico");
        LocalDate fechaCreacion = leerFecha("Fecha creación (YYYY-MM-DD)");
        LocalDate fechaIngreso = leerFecha("Fecha ingreso al museo (YYYY-MM-DD)");
        String sala = leerLinea("Sala");

        ObraFactory.ObraParams.Builder builder = ObraFactory.ObraParams.builder()
            .nombre(nombre)
            .autor(autor)
            .periodo(periodo)
            .valorEconomico(valor)
            .fechaCreacion(fechaCreacion)
            .fechaIngresoMuseo(fechaIngreso)
            .sala(sala);

        if (tipo == ObraFactory.TipoObra.CUADRO) {
            builder.tecnica(leerLinea("Técnica"));
            builder.estilo(leerLinea("Estilo"));
        } else if (tipo == ObraFactory.TipoObra.ESCULTURA) {
            builder.material(leerLinea("Material"));
        }

        Obra obra = catalogoSvc.inscribirObra(tipo, builder.build(), encargado, restaurador);
        println("Obra inscrita: " + obra.getNombre() + " (" + obra.getEstado() + ")");
    }

    private void asignarEstadoObra() {
        Obra obra = seleccionarObra("Obra a cambiar de estado");
        if (obra == null) return;

        println("Estados: ");
        EstadoObra[] valores = EstadoObra.values();
        for (int i = 0; i < valores.length; i++) {
            println((i + 1) + ". " + valores[i]);
        }
        int seleccion = leerEntero("Seleccione un estado", 1, valores.length);
        catalogoSvc.asignarEstado(obra, valores[seleccion - 1], encargado);
        println("Estado actualizado: " + obra.getNombre() + " -> " + obra.getEstado());
    }

    private void listarObras() {
        println("--- Obras en el catálogo ---");
        catalogoSvc.listarObras().forEach(o -> println(o.getDescripcion()));
    }

    private void iniciarRestauracion() {
        Obra obra = seleccionarObra("Obra a restaurar");
        if (obra == null) return;

        Restauracion r = restauracionSvc.iniciar(obra);
        println("Restauración iniciada sobre " + obra.getNombre() + " con inicio " + r.getFechaInicio());
    }

    private void finalizarRestauracion() {
        List<Restauracion> activas = restauracionSvc.consultarTodas().stream()
            .filter(Restauracion::estaActiva)
            .toList();

        if (activas.isEmpty()) {
            println("No hay restauraciones activas.");
            return;
        }

        for (int i = 0; i < activas.size(); i++) {
            Restauracion r = activas.get(i);
            println((i + 1) + ". " + r.getObra().getNombre() + " (inicio: " + r.getFechaInicio() + ")");
        }

        int seleccion = leerEntero("Seleccione restauración para finalizar", 1, activas.size());
        Restauracion seleccionada = activas.get(seleccion - 1);
        restauracionSvc.finalizar(seleccionada);
        println("Restauración finalizada: " + seleccionada.getObra().getNombre());
    }

    private void listarRestauraciones() {
        println("--- Historial de restauraciones ---");
        restauracionSvc.consultarTodas().forEach(r ->
            println(String.format("Obra: %s | inicio: %s | fin: %s | activa: %b",
                r.getObra().getNombre(), r.getFechaInicio(),
                r.getFechaFin() != null ? r.getFechaFin() : "(en curso)",
                r.estaActiva())));
    }

    private void verificarRestauracion() {
        Obra obra = seleccionarObra("Obra a verificar");
        if (obra == null) return;
        println(restauracionSvc.verificarObra(obra).toString());
    }

    private void consultarObrasPorSala() {
        String sala = leerLinea("Sala a consultar");
        List<Obra> obras = visitante.verObras(catalogo, sala);
        if (obras.isEmpty()) {
            println("No hay obras expuestas en sala " + sala);
            return;
        }
        obras.forEach(o -> println(o.getDescripcion()));
    }

    private void registrarMuseo() {
        String nombre = leerLinea("Nombre del museo colaborador");
        String pais = leerLinea("País");
        cesionSvc.registrarMuseo(nombre, pais);
        println("Museo registrado: " + nombre + " (" + pais + ")");
    }

    private void cederObra() {
        Obra obra = seleccionarObra("Obra a ceder");
        if (obra == null) return;
        String museo = leerLinea("Nombre del museo destino");
        double importe = leerDouble("Importe");
        LocalDate inicio = leerFecha("Fecha inicio de cesión (YYYY-MM-DD)");
        LocalDate fin = leerFecha("Fecha fin de cesión (YYYY-MM-DD)");
        Cesion cesion = cesionSvc.cederObra(obra, museo, importe, inicio, fin);
        println("Cesión registrada: " + cesion.getObra().getNombre() + " -> " + museo);
    }

    private void consultarValoracionTotal() {
        double valor = cesionSvc.consultarValoracionTotal(catalogo);
        println("Valoración total del catálogo: $" + String.format("%,.2f", valor));
    }

    private Obra seleccionarObra(String prompt) {
        List<Obra> obras = catalogoSvc.listarObras();
        if (obras.isEmpty()) {
            println("No hay obras disponibles.");
            return null;
        }

        println("--- " + prompt + " ---");
        for (int i = 0; i < obras.size(); i++) {
            Obra o = obras.get(i);
            println((i + 1) + ". " + o.getNombre() + " (" + o.getEstado() + ")");
        }

        int seleccion = leerEntero("Seleccione obra", 1, obras.size());
        return obras.get(seleccion - 1);
    }

    private int leerEntero(String prompt, int min, int max) {
        while (true) {
            String linea = leerLinea(prompt + " [" + min + "-" + max + "]");
            try {
                int valor = Integer.parseInt(linea);
                if (valor < min || valor > max) {
                    println("Debes ingresar un número entre " + min + " y " + max + ".");
                    continue;
                }
                return valor;
            } catch (NumberFormatException ex) {
                println("Entrada no válida. Intenta de nuevo.");
            }
        }
    }

    private double leerDouble(String prompt) {
        while (true) {
            String linea = leerLinea(prompt);
            try {
                double valor = Double.parseDouble(linea);
                if (valor < 0) {
                    println("El valor no puede ser negativo.");
                    continue;
                }
                return valor;
            } catch (NumberFormatException ex) {
                println("Entrada no válida. Ingresa un número válido.");
            }
        }
    }

    private LocalDate leerFecha(String prompt) {
        while (true) {
            String linea = leerLinea(prompt);
            try {
                return LocalDate.parse(linea);
            } catch (DateTimeParseException ex) {
                println("Formato incorrecto. Use YYYY-MM-DD.");
            }
        }
    }

    private String leerLinea(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    private void println(String mensaje) {
        System.out.println(mensaje);
    }
}
