package com.wheely.routes;

import io.javalin.Javalin;
import com.wheely.controller.UsuarioController;

/**
 * Configuración de rutas para usuarios
 * Define todos los endpoints REST para la gestión de usuarios
 */
public class UsuarioRoutes {
    private final UsuarioController usuarioController;

    public UsuarioRoutes(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    /**
     * Registra todas las rutas de usuarios en la aplicación Javalin
     * @param app Instancia de Javalin
     */
    public void register(Javalin app) {
        // Rutas CRUD básicas de usuarios
        app.get("/usuarios", usuarioController::getAll);
        app.get("/usuarios/{id}", usuarioController::getById);
        app.post("/usuarios", usuarioController::create);
        app.put("/usuarios/{id}", usuarioController::update);
        app.delete("/usuarios/{id}", usuarioController::delete);

        // Rutas adicionales de usuarios
        app.post("/usuarios/login", usuarioController::login);
        app.put("/usuarios/{id}/password", usuarioController::changePassword);
    }
}