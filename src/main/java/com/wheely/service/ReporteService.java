package com.wheely.service;

import com.wheely.model.Reporte;
import com.wheely.repository.ReporteRepository;
import com.wheely.repository.UsuarioRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la lógica de negocio de reportes
 * Contiene validaciones y reglas de negocio para reportes
 */
public class ReporteService {
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(ReporteRepository reporteRepository, UsuarioRepository usuarioRepository) {
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene todos los reportes del sistema
     * @return Lista de reportes ordenados por fecha
     * @throws SQLException Error en la consulta
     */
    public List<Reporte> getAllReportes() throws SQLException {
        return reporteRepository.findAll();
    }

    /**
     * Busca un reporte por su ID
     * @param idReporte ID del reporte
     * @return Reporte encontrado o null si no existe
     * @throws SQLException Error en la consulta
     */
    public Reporte getReporteById(int idReporte) throws SQLException {
        return reporteRepository.findById(idReporte);
    }

    /**
     * Obtiene todos los reportes de un usuario específico
     * @param idUsuario ID del usuario
     * @return Lista de reportes del usuario
     * @throws SQLException Error en la consulta
     */
    public List<Reporte> getReportesByUsuario(int idUsuario) throws SQLException {
        // Verificar que el usuario existe
        if (usuarioRepository.findById(idUsuario) == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        return reporteRepository.findByUsuario(idUsuario);
    }

    /**
     * Crea un nuevo reporte
     * @param reporte Reporte a crear
     * @return ID del reporte creado
     * @throws SQLException Error en la base de datos
     * @throws IllegalArgumentException Error de validación
     */
    public int createReporte(Reporte reporte) throws SQLException {
        // Validar datos del reporte
        validateReporte(reporte);

        // Verificar que el usuario existe
        if (usuarioRepository.findById(reporte.getIdUsuario()) == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Validar tipos de reporte válidos (1=Incidencia, 2=Sugerencia, 3=Queja)
        if (reporte.getIdTipoReporte() < 1 || reporte.getIdTipoReporte() > 3) {
            throw new IllegalArgumentException("Tipo de reporte no válido");
        }

        // Validar que idRuta sea positivo
        if (reporte.getIdRuta() <= 0) {
            throw new IllegalArgumentException("ID de ruta no válido");
        }

        return reporteRepository.save(reporte);
    }

    /**
     * Actualiza un reporte existente
     * @param reporte Reporte con datos actualizados
     * @return true si se actualizó correctamente
     * @throws SQLException Error en la base de datos
     * @throws IllegalArgumentException Error de validación
     */
    public boolean updateReporte(Reporte reporte) throws SQLException {
        // Verificar que el reporte existe
        Reporte reporteExistente = reporteRepository.findById(reporte.getIdReporte());
        if (reporteExistente == null) {
            throw new IllegalArgumentException("Reporte no encontrado");
        }

        // Validar datos del reporte
        validateReporte(reporte);

        // Verificar que el usuario existe
        if (usuarioRepository.findById(reporte.getIdUsuario()) == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Validar tipos de reporte válidos
        if (reporte.getIdTipoReporte() < 1 || reporte.getIdTipoReporte() > 3) {
            throw new IllegalArgumentException("Tipo de reporte no válido");
        }

        // Validar que idRuta sea positivo
        if (reporte.getIdRuta() <= 0) {
            throw new IllegalArgumentException("ID de ruta no válido");
        }

        // Verificar que solo el autor puede modificar el reporte
        if (reporteExistente.getIdUsuario() != reporte.getIdUsuario()) {
            throw new IllegalArgumentException("Solo el autor puede modificar este reporte");
        }

        // Mantener la fecha original del reporte
        reporte.setFechaReporte(reporteExistente.getFechaReporte());

        return reporteRepository.update(reporte);
    }

    /**
     * Elimina un reporte por su ID
     * @param idReporte ID del reporte a eliminar
     * @param idUsuarioSolicitante ID del usuario que solicita la eliminación
     * @return true si se eliminó correctamente
     * @throws SQLException Error en la base de datos
     * @throws IllegalArgumentException Error de validación
     */
    public boolean deleteReporte(int idReporte, int idUsuarioSolicitante) throws SQLException {
        // Verificar que el reporte existe
        Reporte reporte = reporteRepository.findById(idReporte);
        if (reporte == null) {
            throw new IllegalArgumentException("Reporte no encontrado");
        }

        // Verificar que solo el autor puede eliminar el reporte
        if (reporte.getIdUsuario() != idUsuarioSolicitante) {
            throw new IllegalArgumentException("Solo el autor puede eliminar este reporte");
        }

        return reporteRepository.delete(idReporte);
    }

    /**
     * Obtiene estadísticas básicas de reportes
     * @return Información estadística
     * @throws SQLException Error en la consulta
     */
    public ReporteStats getReporteStats() throws SQLException {
        int totalReportes = reporteRepository.count();
        List<Reporte> todosReportes = reporteRepository.findAll();

        // Contar reportes por tipo
        long incidencias = todosReportes.stream().filter(r -> r.getIdTipoReporte() == 1).count();
        long sugerencias = todosReportes.stream().filter(r -> r.getIdTipoReporte() == 2).count();
        long quejas = todosReportes.stream().filter(r -> r.getIdTipoReporte() == 3).count();

        // Contar reportes del último mes
        LocalDateTime unMesAtras = LocalDateTime.now().minusMonths(1);
        long reportesUltimoMes = todosReportes.stream()
                .filter(r -> r.getFechaReporte() != null && r.getFechaReporte().isAfter(unMesAtras))
                .count();

        return new ReporteStats(totalReportes, (int)incidencias, (int)sugerencias, (int)quejas, (int)reportesUltimoMes);
    }

    /**
     * Valida los datos básicos de un reporte
     * @param reporte Reporte a validar
     * @throws IllegalArgumentException Si los datos no son válidos
     */
    private void validateReporte(Reporte reporte) {
        if (reporte == null) {
            throw new IllegalArgumentException("Los datos del reporte son requeridos");
        }

        if (reporte.getTitulo() == null || reporte.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es requerido");
        }

        if (reporte.getDescripcion() == null || reporte.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es requerida");
        }

        // Validar longitud del título
        if (reporte.getTitulo().trim().length() > 100) {
            throw new IllegalArgumentException("El título no puede exceder 100 caracteres");
        }

        // Validar longitud de la descripción
        if (reporte.getDescripcion().trim().length() > 1000) {
            throw new IllegalArgumentException("La descripción no puede exceder 1000 caracteres");
        }

        // Validar que los IDs sean positivos
        if (reporte.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("ID de usuario no válido");
        }
    }

    /**
     * Clase interna para estadísticas de reportes
     */
    public static class ReporteStats {
        private int totalReportes;
        private int incidencias;
        private int sugerencias;
        private int quejas;
        private int reportesUltimoMes;

        public ReporteStats(int totalReportes, int incidencias, int sugerencias, int quejas, int reportesUltimoMes) {
            this.totalReportes = totalReportes;
            this.incidencias = incidencias;
            this.sugerencias = sugerencias;
            this.quejas = quejas;
            this.reportesUltimoMes = reportesUltimoMes;
        }

        // Getters
        public int getTotalReportes() { return totalReportes; }
        public int getIncidencias() { return incidencias; }
        public int getSugerencias() { return sugerencias; }
        public int getQuejas() { return quejas; }
        public int getReportesUltimoMes() { return reportesUltimoMes; }
    }
}