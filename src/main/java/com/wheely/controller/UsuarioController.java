package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.model.Usuario;
import com.wheely.service.UsuarioService;
import com.wheely.util.ApiResponse;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador REST para gestión de usuarios
 * Maneja todos los endpoints HTTP para operaciones CRUD de usuarios
 */
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * GET /usuarios - Obtiene todos los usuarios
     */
    public void getAll(Context ctx) {
        try {
            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            ApiResponse response = ApiResponse.success("Usuarios obtenidos correctamente", usuarios);
            ctx.status(HttpStatus.OK).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al obtener usuarios: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * GET /usuarios/{id} - Obtiene un usuario por ID
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = usuarioService.getUsuarioById(id);

            if (usuario != null) {
                ApiResponse response = ApiResponse.success("Usuario encontrado", usuario);
                ctx.status(HttpStatus.OK).json(response);
            } else {
                ApiResponse response = ApiResponse.error("Usuario no encontrado");
                ctx.status(HttpStatus.NOT_FOUND).json(response);
            }
        } catch (NumberFormatException e) {
            ApiResponse response = ApiResponse.error("ID de usuario no válido");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al obtener usuario: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * POST /usuarios - Crea un nuevo usuario
     */
    public void create(Context ctx) {
        try {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            int idCreado = usuarioService.createUsuario(usuario);

            // Retornar el usuario creado sin contraseña
            usuario.setIdUser(idCreado);
            usuario.setPassword("");

            ApiResponse response = ApiResponse.success("Usuario creado correctamente", usuario);
            ctx.status(HttpStatus.CREATED).json(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al crear usuario: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * PUT /usuarios/{id} - Actualiza un usuario existente
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            usuario.setIdUser(id);

            boolean actualizado = usuarioService.updateUsuario(usuario);

            if (actualizado) {
                // Obtener el usuario actualizado sin contraseña
                Usuario usuarioActualizado = usuarioService.getUsuarioById(id);
                ApiResponse response = ApiResponse.success("Usuario actualizado correctamente", usuarioActualizado);
                ctx.status(HttpStatus.OK).json(response);
            } else {
                ApiResponse response = ApiResponse.error("No se pudo actualizar el usuario");
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
            }
        } catch (NumberFormatException e) {
            ApiResponse response = ApiResponse.error("ID de usuario no válido");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al actualizar usuario: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * DELETE /usuarios/{id} - Elimina un usuario
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            boolean eliminado = usuarioService.deleteUsuario(id);

            if (eliminado) {
                ApiResponse response = ApiResponse.success("Usuario eliminado correctamente");
                ctx.status(HttpStatus.OK).json(response);
            } else {
                ApiResponse response = ApiResponse.error("No se pudo eliminar el usuario");
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
            }
        } catch (NumberFormatException e) {
            ApiResponse response = ApiResponse.error("ID de usuario no válido");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al eliminar usuario: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * POST /usuarios/login - Autentica un usuario
     */
    public void login(Context ctx) {
        try {
            // Obtener credenciales del body JSON
            var credentials = ctx.bodyAsClass(LoginRequest.class);

            Usuario usuario = usuarioService.authenticateUsuario(credentials.email, credentials.password);

            if (usuario != null) {
                ApiResponse response = ApiResponse.success("Login exitoso", usuario);
                ctx.status(HttpStatus.OK).json(response);
            } else {
                ApiResponse response = ApiResponse.error("Credenciales incorrectas");
                ctx.status(HttpStatus.UNAUTHORIZED).json(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error en el login: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * PUT /usuarios/{id}/password - Cambia la contraseña de un usuario
     */
    public void changePassword(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            var passwordRequest = ctx.bodyAsClass(ChangePasswordRequest.class);

            boolean cambiado = usuarioService.changePassword(id, passwordRequest.currentPassword, passwordRequest.newPassword);

            if (cambiado) {
                ApiResponse response = ApiResponse.success("Contraseña cambiada correctamente");
                ctx.status(HttpStatus.OK).json(response);
            } else {
                ApiResponse response = ApiResponse.error("No se pudo cambiar la contraseña");
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
            }
        } catch (NumberFormatException e) {
            ApiResponse response = ApiResponse.error("ID de usuario no válido");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al cambiar contraseña: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * Clase para recibir credenciales de login
     */
    public static class LoginRequest {
        public String email;
        public String password;
    }

    /**
     * Clase para recibir solicitud de cambio de contraseña
     */
    public static class ChangePasswordRequest {
        public String currentPassword;
        public String newPassword;
    }
}