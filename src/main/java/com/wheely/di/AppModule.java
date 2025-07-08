package com.wheely.di;

import com.wheely.controller.UsuarioController;
import com.wheely.controller.ReporteController;
import com.wheely.repository.UsuarioRepository;
import com.wheely.repository.ReporteRepository;
import com.wheely.routes.UsuarioRoutes;
import com.wheely.routes.ReporteRoutes;
import com.wheely.service.UsuarioService;
import com.wheely.service.ReporteService;

/**
 * Módulo de configuración de dependencias de la aplicación
 * Implementa un patrón de inyección de dependencias manual
 * Crea e inicializa todas las capas de la aplicación
 */
public class AppModule {

    /**
     * Inicializa y configura todo el módulo de usuarios
     * Crea la cadena completa: Repository -> Service -> Controller -> Routes
     * @return Instancia configurada de UsuarioRoutes
     */
    public static UsuarioRoutes initUsuarios() {
        // Capa de acceso a datos
        UsuarioRepository usuarioRepository = new UsuarioRepository();

        // Capa de lógica de negocio
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);

        // Capa de controladores
        UsuarioController usuarioController = new UsuarioController(usuarioService);

        // Capa de rutas
        return new UsuarioRoutes(usuarioController);
    }

    /**
     * Inicializa y configura todo el módulo de reportes
     * Crea la cadena completa: Repository -> Service -> Controller -> Routes
     * @return Instancia configurada de ReporteRoutes
     */
    public static ReporteRoutes initReportes() {
        // Capa de acceso a datos
        UsuarioRepository usuarioRepository = new UsuarioRepository();
        ReporteRepository reporteRepository = new ReporteRepository();

        // Capa de lógica de negocio (ReporteService necesita UsuarioRepository para validaciones)
        ReporteService reporteService = new ReporteService(reporteRepository, usuarioRepository);

        // Capa de controladores
        ReporteController reporteController = new ReporteController(reporteService);

        // Capa de rutas
        return new ReporteRoutes(reporteController);
    }
}