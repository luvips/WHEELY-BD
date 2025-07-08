package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.ReporteController;

/**
 * Configuración de rutas para reportes
 * Define todos los endpoints REST para la gestión de reportes
 */
public class ReporteRoutes {
    private final ReporteController reporteController;

    public ReporteRoutes(ReporteController reporteController) {
        this.reporteController = reporteController;
    }

    /**
     * Registra todas las rutas de reportes en la aplicación Javalin
     * @param app Instancia de Javalin
     */
    public void register(Javalin app) {
        // Rutas CRUD básicas de reportes
        app.get("/reportes", reporteController::getAll);
        app.get("/reportes/{id}", reporteController::getById);
        app.post("/reportes", reporteController::create);
        app.put("/reportes/{id}", reporteController::update);
        app.delete("/reportes/{id}", reporteController::delete);

        // Rutas adicionales de reportes
        app.get("/reportes/usuario/{usuarioId}", reporteController::getByUsuario);
        app.get("/reportes/stats", reporteController::getStats);
        app.get("/reportes/tipos", reporteController::getTiposReporte);
    }
}