package com.wheely;

import io.javalin.Javalin;
import io.github.cdimascio.dotenv.Dotenv;
import com.wheely.di.AppModule;
import com.wheely.routes.UsuarioRoutes;
import com.wheely.routes.ReporteRoutes;
import com.wheely.util.ApiResponse;

/**
 * Clase principal de la aplicación Wheely API
 * Configura y levanta el servidor Javalin con todos los endpoints
 */
public class Main {

    public static void main(String[] args) {
        // Cargar variables de entorno
        Dotenv dotenv = Dotenv.load();
        int port = Integer.parseInt(dotenv.get("SERVER_PORT", "7000"));

        // Crear aplicación Javalin básica
        Javalin app = Javalin.create();

        // Configurar CORS manualmente antes de cualquier ruta
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        // Manejar OPTIONS requests para CORS
        app.options("/*", ctx -> ctx.status(200));

        // Configurar rutas base
        configurarRutasBase(app);

        // Inicializar y registrar módulos de la aplicación
        inicializarModulos(app);

        // Configurar manejo de errores globales
        configurarManejadorErrores(app);

        // Levantar el servidor
        app.start(port);

        // Mensaje de confirmación
        System.out.println("=================================================");
        System.out.println("Wheely API iniciada correctamente");
        System.out.println("Servidor ejecutandose en: http://localhost:" + port);
        System.out.println("Base de datos: " + dotenv.get("DB_SCHEMA"));
        System.out.println("=================================================");
    }

    /**
     * Configura las rutas base de la API
     */
    private static void configurarRutasBase(Javalin app) {
        // Ruta de verificación de estado
        app.get("/", ctx -> {
            ApiResponse response = ApiResponse.success("Wheely API funcionando correctamente",
                    "Sistema de transporte público de Tuxtla Gutiérrez");
            ctx.json(response);
        });

        // Ruta de información de la API
        app.get("/info", ctx -> {
            ApiInfo info = new ApiInfo();
            ApiResponse response = ApiResponse.success("Información de la API", info);
            ctx.json(response);
        });

        // Ruta de verificación de salud
        app.get("/health", ctx -> {
            try {
                // Verificar conexión a base de datos
                com.wheely.config.DatabaseConfig.getDataSource().getConnection().close();

                HealthStatus health = new HealthStatus("OK", "Base de datos conectada", System.currentTimeMillis());
                ApiResponse response = ApiResponse.success("Sistema saludable", health);
                ctx.json(response);
            } catch (Exception e) {
                HealthStatus health = new HealthStatus("ERROR", "Error de conexión: " + e.getMessage(), System.currentTimeMillis());
                ApiResponse response = ApiResponse.error("Error en el sistema", health);
                ctx.status(500).json(response);
            }
        });
    }

    /**
     * Inicializa y registra todos los módulos de la aplicación
     */
    private static void inicializarModulos(Javalin app) {
        // Inicializar módulo de usuarios
        UsuarioRoutes usuarioRoutes = AppModule.initUsuarios();
        usuarioRoutes.register(app);

        // Inicializar módulo de reportes
        ReporteRoutes reporteRoutes = AppModule.initReportes();
        reporteRoutes.register(app);

        System.out.println("Módulos de usuarios y reportes inicializados");
    }

    /**
     * Configura el manejo global de errores
     */
    private static void configurarManejadorErrores(Javalin app) {
        // Manejo de errores 404
        app.error(404, ctx -> {
            ApiResponse response = ApiResponse.error("Endpoint no encontrado");
            ctx.json(response);
        });

        // Manejo de errores 500
        app.error(500, ctx -> {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.json(response);
        });

        // Manejo de excepciones no capturadas
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace(); // Log del error para debugging
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(500).json(response);
        });
    }

    /**
     * Clase para información de la API
     */
    public static class ApiInfo {
        public String nombre = "Wheely API";
        public String version = "1.0.0";
        public String descripcion = "API REST para el sistema de transporte público de Tuxtla Gutiérrez";
        public String[] endpoints = {
                "GET /usuarios - Obtener usuarios",
                "POST /usuarios - Crear usuario",
                "PUT /usuarios/{id} - Actualizar usuario",
                "DELETE /usuarios/{id} - Eliminar usuario",
                "POST /usuarios/login - Login de usuario",
                "GET /reportes - Obtener reportes",
                "POST /reportes - Crear reporte",
                "PUT /reportes/{id} - Actualizar reporte",
                "DELETE /reportes/{id} - Eliminar reporte"
        };
        public String documentacion = "Usar Insomnia para probar los endpoints";
        public long timestamp = System.currentTimeMillis();
    }

    /**
     * Clase para estado de salud del sistema
     */
    public static class HealthStatus {
        public String status;
        public String message;
        public long timestamp;

        public HealthStatus(String status, String message, long timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}