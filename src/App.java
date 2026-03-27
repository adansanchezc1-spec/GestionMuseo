import com.model.enums.EstadoObra;
import com.model.infraestructure.ObraFactory.ObraParams;
import com.model.infraestructure.ObraFactory.TipoObra;
import com.model.infraestructure.Repositories.CesionRepository;
import com.model.infraestructure.Repositories.MuseoColaboradorRepository;
import com.model.infraestructure.Repositories.RestauracionRepository;
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

public class App {

    public static void main(String[] args) {

        // ── Composition Root ─────────────────────────────────────────────────
        Catalogo.resetInstancia();
        Catalogo catalogo = Catalogo.obtenerInstancia();

        EncargadoCatalogo encargado       = new EncargadoCatalogo("Ana García");
        RestauradorJefe   restauradorJefe = new RestauradorJefe("Luis Martínez");
        Visitante         visitante       = new Visitante("Pedro Pérez");
        Director          director        = new Director("Carmen López");

        RestauracionRepository        restauracionRepo = new RestauracionRepository();
        CesionRepository              cesionRepo       = new CesionRepository();
        MuseoColaboradorRepository    museoRepo        = new MuseoColaboradorRepository();

        CatalogoService      catalogoSvc      = new CatalogoService(catalogo, restauracionRepo);
        RestauracionService  restauracionSvc  = new RestauracionService(restauradorJefe, restauracionRepo);
        CesionService        cesionSvc        = new CesionService(director, cesionRepo, museoRepo);
        AutenticacionService authSvc          = new AutenticacionService();

        // ── Inicializar catálogo con obras por defecto ──────────────────────
        catalogoSvc.inscribirObra(
            TipoObra.CUADRO,
            ObraParams.builder()
                .nombre("La Gioconda").autor("Leonardo da Vinci")
                .periodo("Renacimiento").valorEconomico(900_000_000)
                .fechaCreacion(LocalDate.of(1503, 1, 1))
                .fechaIngresoMuseo(LocalDate.of(1797, 1, 1))
                .sala("Sala A").tecnica("Óleo sobre tabla").estilo("Sfumato")
                .build(),
            encargado, restauradorJefe
        );

        catalogoSvc.inscribirObra(
            TipoObra.ESCULTURA,
            ObraParams.builder()
                .nombre("Venus de Milo").autor("Alejandros de Antioquía")
                .periodo("Helenismo").valorEconomico(500_000_000)
                .fechaCreacion(LocalDate.of(-100, 1, 1))
                .fechaIngresoMuseo(LocalDate.of(1821, 1, 1))
                .sala("Sala B").material("Mármol")
                .build(),
            encargado, restauradorJefe
        );

        catalogoSvc.inscribirObra(
            TipoObra.OTRO_OBJETO,
            ObraParams.builder()
                .nombre("Jarrón Han").autor("Desconocido")
                .periodo("Dinastía Han").valorEconomico(2_500_000)
                .fechaCreacion(LocalDate.of(100, 1, 1))
                .fechaIngresoMuseo(LocalDate.of(1960, 6, 15))
                .sala("Sala C")
                .build(),
            encargado, restauradorJefe
        );

        // Por defecto iniciar en modo interactivo, para elegir usuario y operación según rol.
        if (args.length == 0 || "-i".equals(args[0]) || "--interactivo".equalsIgnoreCase(args[0])) {
            new ConsolaInteractiva(
                catalogo, encargado, restauradorJefe, visitante, director,
                catalogoSvc, restauracionSvc, cesionSvc, authSvc
            ).ejecutar();
            return;
        }

        // Para ejecutar la demo completa con salida predefinida usar --demo.
        if ("--demo".equalsIgnoreCase(args[0])) {
            sep("SISTEMA DE GESTIÓN DE MUSEO — Demo completa");
        }

        // ── 1. Autenticación ─────────────────────────────────────────────────
        titulo("1. Autenticación de usuarios");
        System.out.println("   Encargado : " + authSvc.autenticar(encargado, "clave123"));
        System.out.println("   Director  : " + authSvc.autenticar(director,  "directorclave"));

        // ── 2. Inscripción de obras (Factory + Observer) ──────────────────
        titulo("2. Inscripción de obras en el catálogo");

        Obra gioconda = catalogoSvc.inscribirObra(
            TipoObra.CUADRO,
            ObraParams.builder()
                .nombre("La Gioconda").autor("Leonardo da Vinci")
                .periodo("Renacimiento").valorEconomico(900_000_000)
                .fechaCreacion(LocalDate.of(1503, 1, 1))
                .fechaIngresoMuseo(LocalDate.of(1797, 1, 1))
                .sala("Sala A").tecnica("Óleo sobre tabla").estilo("Sfumato")
                .build(),
            encargado, restauradorJefe
        );

        Obra venus = catalogoSvc.inscribirObra(
            TipoObra.ESCULTURA,
            ObraParams.builder()
                .nombre("Venus de Milo").autor("Alejandros de Antioquía")
                .periodo("Helenismo").valorEconomico(500_000_000)
                .fechaCreacion(LocalDate.of(-100, 1, 1))
                .fechaIngresoMuseo(LocalDate.of(1821, 1, 1))
                .sala("Sala B").material("Mármol")
                .build(),
            encargado, restauradorJefe
        );

        Obra jarron = catalogoSvc.inscribirObra(
            TipoObra.OTRO_OBJETO,
            ObraParams.builder()
                .nombre("Jarrón Han").autor("Desconocido")
                .periodo("Dinastía Han").valorEconomico(2_500_000)
                .fechaCreacion(LocalDate.of(100, 1, 1))
                .fechaIngresoMuseo(LocalDate.of(1960, 6, 15))
                .sala("Sala C")
                .build(),
            encargado, restauradorJefe
        );

        catalogoSvc.listarObras()
            .forEach(o -> System.out.println("   + " + o.getNombre()));

        // ── 3. Consulta por sala (Visitante) ──────────────────────────────
        titulo("3. Obras expuestas en Sala A (visitante)");
        visitante.verObras(catalogo, "Sala A")
            .forEach(o -> System.out.println(o.getDescripcion()));

        // ── 4. Restauración manual ────────────────────────────────────────
        titulo("4. Restauración manual de la Venus de Milo");
        Restauracion restVenus = restauracionSvc.iniciar(venus);
        System.out.println("   Estado Venus: " + venus.getEstado());
        restauracionSvc.finalizar(restVenus);
        System.out.println("   Venus restaurada. Estado: " + venus.getEstado());

        // ── 5. Estado DAÑADA → Observer dispara restauración automática ───
        titulo("5. Marcando La Gioconda como DAÑADA (Observer en acción)");
        catalogoSvc.asignarEstado(gioconda, EstadoObra.DANIADA, encargado);
        System.out.println("   Estado Gioconda tras Observer: " + gioconda.getEstado());

        // ── 6. Historial de restauraciones ────────────────────────────────
        titulo("6. Historial de restauraciones (ordenado por antigüedad)");
        restauracionSvc.consultarTodas().forEach(r ->
            System.out.printf("   - %-20s | inicio: %s | activa: %b%n",
                r.getObra().getNombre(), r.getFechaInicio(), r.estaActiva()));

        // ── 7. Verificación automática ────────────────────────────────────
        titulo("7. Verificación automática de la Venus de Milo");
        RestauradorJefe.VerificacionResult vr = restauracionSvc.verificarObra(venus);
        System.out.println("   " + vr);

        // ── 8. Cesión a museo colaborador ────────────────────────────────
        titulo("8. Cesión del Jarrón Han al Museo de Berlín");
        cesionSvc.registrarMuseo("Museo de Berlín", "Alemania");
        Cesion cesion = cesionSvc.cederObra(
            jarron, "Museo de Berlín", 50_000,
            LocalDate.now(),
            LocalDate.now().plusYears(1)
        );
        System.out.println("   Cesión vigente : " + cesion.estaVigente());
        System.out.println("   Estado Jarrón  : " + jarron.getEstado());
        System.out.println("   Sala Jarrón    : " + jarron.getSala());

        // ── 9. Valoración total ───────────────────────────────────────────
        titulo("9. Valoración total del catálogo");
        System.out.printf("   Total: $%,.0f%n",
            cesionSvc.consultarValoracionTotal(catalogo));

        // ── 10. Menús por rol ─────────────────────────────────────────────
        titulo("10. Menús por rol");
        for (Usuario u : new Usuario[]{encargado, restauradorJefe, visitante, director}) {
            System.out.printf("%n   [%s] %s%n", u.getRol(), u.getNombre());
            u.mostrarMenu().forEach(item -> System.out.println("     " + item));
        }

        sep("Demo completada exitosamente ✔");
    }

    // ── Helpers de formato ────────────────────────────────────────────────────
    private static void sep(String msg) {
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("   " + msg);
        System.out.println("══════════════════════════════════════════════════");
    }

    private static void titulo(String msg) {
        System.out.println("\n " + msg);
    }
}