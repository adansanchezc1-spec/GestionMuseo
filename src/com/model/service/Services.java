package com.model.service;

import com.model.enums.EstadoObra;
import com.model.infraestructure.ObraFactory;
import com.model.infraestructure.Repositories.CesionRepository;
import com.model.infraestructure.Repositories.MuseoColaboradorRepository;
import com.model.infraestructure.Repositories.RestauracionRepository;
import com.model.infraestructure.RestauracionObservador;
import com.model.model.*;
import java.time.LocalDate;
import java.util.List;

public final class Services {

    private Services() {}

    // ─── CatalogoService ──────────────────────────────────────────────────────

    public static class CatalogoService {

        private final Catalogo                catalogo;
        private final RestauracionRepository  restauracionRepo;

        public CatalogoService(Catalogo catalogo,
                               RestauracionRepository restauracionRepo) {
            this.catalogo         = catalogo;
            this.restauracionRepo = restauracionRepo;
        }

        /**
         * Crea una obra con la Factory, le adjunta el Observer y la inscribe.
         *
         * @param tipo           tipo de obra (ObraFactory.TipoObra)
         * @param params         parámetros de construcción
         * @param encargado      quien realiza la inscripción
         * @param restauradorJefe para conectar el Observer de estado DAÑADA
         * @return obra creada y registrada
         */
        public Obra inscribirObra(ObraFactory.TipoObra tipo,
                                  ObraFactory.ObraParams params,
                                  EncargadoCatalogo encargado,
                                  RestauradorJefe restauradorJefe) {
            Obra obra = ObraFactory.crear(tipo, params);
            // GOF Observer: conectar restauración automática al Observer
            obra.suscribir(new RestauracionObservador(restauradorJefe, restauracionRepo));
            encargado.inscribirObra(obra, catalogo);
            return obra;
        }

        public void asignarEstado(Obra obra, EstadoObra estado,
                                  EncargadoCatalogo encargado) {
            encargado.asignarEstado(obra, estado);
        }

        public List<Obra> listarObras()              { return catalogo.listarObras(); }
        public List<Obra> listarPorSala(String sala) { return catalogo.listarPorSala(sala); }
        public double     valoracionTotal()          { return catalogo.valoracionTotal(); }
    }

    // ─── RestauracionService ──────────────────────────────────────────────────

    public static class RestauracionService {

        private final RestauradorJefe        restauradorJefe;
        private final RestauracionRepository repo;

        public RestauracionService(RestauradorJefe restauradorJefe,
                                   RestauracionRepository repo) {
            this.restauradorJefe = restauradorJefe;
            this.repo            = repo;
        }

        /**
         * Inicia una restauración y la persiste en el repositorio.
         *
         * @param obra obra a restaurar
         * @return la restauración creada
         */
        public Restauracion iniciar(Obra obra) {
            Restauracion r = restauradorJefe.iniciarRestauracion(obra, LocalDate.now());
            obra.asignarEstado(EstadoObra.EN_REPARACION);
            repo.guardar(r);
            return r;
        }

        /**
         * Finaliza la restauración y devuelve la obra al estado EXPUESTA.
         *
         * @param restauracion restauración activa a cerrar
         * @param fechaFin     fecha de cierre
         */
        public void finalizar(Restauracion restauracion, LocalDate fechaFin) {
            restauradorJefe.finalizarRestauracion(restauracion, fechaFin);
            restauracion.getObra().asignarEstado(EstadoObra.EXPUESTA);
        }

        public void finalizar(Restauracion restauracion) {
            finalizar(restauracion, LocalDate.now());
        }

        /** @return todas las restauraciones ordenadas por antigüedad ascendente */
        public List<Restauracion> consultarTodas() {
            return repo.findTodasOrdenadasPorAntiguedad();
        }

        public RestauradorJefe.VerificacionResult verificarObra(Obra obra) {
            return restauradorJefe.verificarObraParaRestaurar(obra);
        }
    }

    // ─── CesionService ────────────────────────────────────────────────────────

    public static class CesionService {

        private final Director                    director;
        private final CesionRepository            cesionRepo;
        private final MuseoColaboradorRepository  museoRepo;

        public CesionService(Director director,
                             CesionRepository cesionRepo,
                             MuseoColaboradorRepository museoRepo) {
            this.director   = director;
            this.cesionRepo = cesionRepo;
            this.museoRepo  = museoRepo;
        }

        /**
         * Registra un museo colaborador en el sistema.
         *
         * @param nombre nombre del museo
         * @param pais   país del museo
         * @return museo registrado
         */
        public MuseoColaborador registrarMuseo(String nombre, String pais) {
            MuseoColaborador museo = new MuseoColaborador(nombre, pais);
            museoRepo.guardar(museo);
            director.gestionarMuseos(museo);
            return museo;
        }

        /**
         * Cede una obra a un museo colaborador y persiste la cesión.
         *
         * @param obra        obra a ceder
         * @param nombreMuseo nombre del museo destino (debe existir en el repositorio)
         * @param importe     importe pactado
         * @param inicio      inicio del período
         * @param fin         fin del período
         * @return la cesión creada
         */
        public Cesion cederObra(Obra obra, String nombreMuseo,
                                double importe, LocalDate inicio, LocalDate fin) {
            MuseoColaborador museo = museoRepo.findByNombre(nombreMuseo)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Museo \"" + nombreMuseo + "\" no encontrado."));
            Cesion cesion = director.cederObra(obra, museo, importe, inicio, fin);
            cesionRepo.guardar(cesion);
            return cesion;
        }

        public double consultarValoracionTotal(Catalogo catalogo) {
            return director.consultarValoracionTotal(catalogo);
        }
    }

    // ─── AutenticacionService ─────────────────────────────────────────────────

    public static class AutenticacionService {

        /**
         * Autentica a un usuario con su contraseña.
         * GRASP Protected Variations: el criterio de autenticación puede cambiar
         * en las subclases sin afectar este servicio.
         *
         * @param usuario    usuario a autenticar
         * @param contrasena credencial
         * @return true si la autenticación es correcta
         * @throws SecurityException si la autenticación falla
         */
        public boolean autenticar(Usuario usuario, String contrasena) {
            if (!usuario.autenticar(contrasena)) {
                throw new SecurityException(
                    "Autenticación fallida para el usuario \"" + usuario.getNombre() + "\".");
            }
            return true;
        }
    }
}