package com.wheely.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import com.wheely.model.Reporte;
import com.wheely.service.ReporteService;
import com.wheely.util.ApiResponse;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador REST para gestión de reportes
 * Maneja todos los endpoints HTTP para operaciones CRUD de reportes
 */
public class ReporteController {
    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /**
     * GET /reportes - Obtiene todos los reportes
     */
    public void getAll(Context ctx) {
        try {
            List<Reporte> reportes = reporteService.getAllReportes();
            ApiResponse response = ApiResponse.success("Reportes obtenidos correctamente", reportes);
            ctx.status(HttpStatus.OK).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al obtener reportes: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * GET /reportes/{id} - Obtiene un reporte por ID
     */
    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Reporte reporte = reporteService.getReporteById(id);

            if (reporte != null) {
                ApiResponse response = ApiResponse.success("Reporte encontrado", reporte);
                ctx.status(HttpStatus.OK).json(response);
            } else {
                ApiResponse response = ApiResponse.error("Reporte no encontrado");
                ctx.status(HttpStatus.NOT_FOUND).json(response);
            }
        } catch (NumberFormatException e) {
            ApiResponse response = ApiResponse.error("ID de reporte no válido");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al obtener reporte: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * POST /reportes - Crea un nuevo reporte
     */
    public void create(Context ctx) {
        try {
            Reporte reporte = ctx.bodyAsClass(Reporte.class);
            int idCreado = reporteService.createReporte(reporte);

            // Retornar el reporte creado
            reporte.setIdReporte(idCreado);

            ApiResponse response = ApiResponse.success("Reporte creado correctamente", reporte);
            ctx.status(HttpStatus.CREATED).json(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al crear reporte: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * PUT /reportes/{id} - Actualiza un reporte existente
     */
    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Reporte reporte = ctx.bodyAsClass(Reporte.class);
            reporte.setIdReporte(id);

            boolean actualizado = reporteService.updateReporte(reporte);

            if (actualizado) {
                // Obtener el reporte actualizado
                Reporte reporteActualizado = reporteService.getReporteById(id);
                ApiResponse response = ApiResponse.success("Reporte actualizado correctamente", reporteActualizado);
                ctx.status(HttpStatus.OK).json(response);
            } else {
                ApiResponse response = ApiResponse.error("No se pudo actualizar el reporte");
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
            }
        } catch (NumberFormatException e) {
            ApiResponse response = ApiResponse.error("ID de reporte no válido");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al actualizar reporte: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * DELETE /reportes/{id} - Elimina un reporte
     * Requiere parámetro query 'usuarioId' para verificar autorización
     */
    public void delete(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));

            // Obtener ID del usuario desde query parameter
            String usuarioIdParam = ctx.queryParam("usuarioId");
            if (usuarioIdParam == null || usuarioIdParam.trim().isEmpty()) {
                ApiResponse response = ApiResponse.error("ID de usuario requerido para eliminar reporte");
                ctx.status(HttpStatus.BAD_REQUEST).json(response);
                return;
            }

            int usuarioId = Integer.parseInt(usuarioIdParam);
            boolean eliminado = reporteService.deleteReporte(id, usuarioId);

            if (eliminado) {
                ApiResponse response = ApiResponse.success("Reporte eliminado correctamente");
                ctx.status(HttpStatus.OK).json(response);
            } else {
                ApiResponse response = ApiResponse.error("No se pudo eliminar el reporte");
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
            }
        } catch (NumberFormatException e) {
            ApiResponse response = ApiResponse.error("ID no válido");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al eliminar reporte: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * GET /reportes/usuario/{usuarioId} - Obtiene reportes de un usuario específico
     */
    public void getByUsuario(Context ctx) {
        try {
            int usuarioId = Integer.parseInt(ctx.pathParam("usuarioId"));
            List<Reporte> reportes = reporteService.getReportesByUsuario(usuarioId);

            ApiResponse response = ApiResponse.success("Reportes del usuario obtenidos correctamente", reportes);
            ctx.status(HttpStatus.OK).json(response);
        } catch (NumberFormatException e) {
            ApiResponse response = ApiResponse.error("ID de usuario no válido");
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (IllegalArgumentException e) {
            ApiResponse response = ApiResponse.error("Error de validación: " + e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al obtener reportes: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * GET /reportes/stats - Obtiene estadísticas de reportes
     */
    public void getStats(Context ctx) {
        try {
            ReporteService.ReporteStats stats = reporteService.getReporteStats();
            ApiResponse response = ApiResponse.success("Estadísticas obtenidas correctamente", stats);
            ctx.status(HttpStatus.OK).json(response);
        } catch (SQLException e) {
            ApiResponse response = ApiResponse.error("Error al obtener estadísticas: " + e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * GET /reportes/tipos - Obtiene los tipos de reportes disponibles
     */
    public void getTiposReporte(Context ctx) {
        try {
            // Retornar los tipos de reporte disponibles
            var tipos = List.of(
                    new TipoReporte(1, "Incidencia", "Problemas relacionados con el servicio de transporte"),
                    new TipoReporte(2, "Sugerencia", "Propuestas de mejora para el sistema de transporte"),
                    new TipoReporte(3, "Queja", "Inconformidades sobre el servicio o comportamiento")
            );

            ApiResponse response = ApiResponse.success("Tipos de reporte obtenidos correctamente", tipos);
            ctx.status(HttpStatus.OK).json(response);
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("Error interno del servidor");
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(response);
        }
    }

    /**
     * Clase para representar tipos de reporte
     */
    public static class TipoReporte {
        public int id;
        public String nombre;
        public String descripcion;

        public TipoReporte(int id, String nombre, String descripcion) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
        }
    }
}