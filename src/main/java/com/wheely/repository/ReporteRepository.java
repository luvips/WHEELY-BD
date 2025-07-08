package com.wheely.repository;

import com.wheely.config.DatabaseConfig;
import com.wheely.model.Reporte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio para operaciones CRUD de la tabla reporte_estado_ruta
 * Maneja todas las interacciones con la base de datos para reportes
 */
public class ReporteRepository {

    /**
     * Obtiene todos los reportes de la base de datos
     * @return Lista de todos los reportes
     * @throws SQLException Error en la consulta
     */
    public List<Reporte> findAll() throws SQLException {
        List<Reporte> reportes = new ArrayList<>();
        String query = "SELECT idReporte_Estado_Ruta, idRuta, idTipo_Reporte, idUsuario, " +
                "titulo, descripcion, fecha_reporte FROM reporte_estado_ruta " +
                "ORDER BY fecha_reporte DESC";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reporte reporte = new Reporte();
                reporte.setIdReporte(rs.getInt("idReporte_Estado_Ruta"));
                reporte.setIdRuta(rs.getInt("idRuta"));
                reporte.setIdTipoReporte(rs.getInt("idTipo_Reporte"));
                reporte.setIdUsuario(rs.getInt("idUsuario"));
                reporte.setTitulo(rs.getString("titulo"));
                reporte.setDescripcion(rs.getString("descripcion"));

                // Convertir Timestamp a LocalDateTime
                Timestamp timestamp = rs.getTimestamp("fecha_reporte");
                if (timestamp != null) {
                    reporte.setFechaReporte(timestamp.toLocalDateTime());
                }

                reportes.add(reporte);
            }
        }
        return reportes;
    }

    /**
     * Busca un reporte por su ID
     * @param idReporte ID del reporte a buscar
     * @return Reporte encontrado o null si no existe
     * @throws SQLException Error en la consulta
     */
    public Reporte findById(int idReporte) throws SQLException {
        Reporte reporte = null;
        String query = "SELECT idReporte_Estado_Ruta, idRuta, idTipo_Reporte, idUsuario, " +
                "titulo, descripcion, fecha_reporte FROM reporte_estado_ruta " +
                "WHERE idReporte_Estado_Ruta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idReporte);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reporte = new Reporte();
                    reporte.setIdReporte(rs.getInt("idReporte_Estado_Ruta"));
                    reporte.setIdRuta(rs.getInt("idRuta"));
                    reporte.setIdTipoReporte(rs.getInt("idTipo_Reporte"));
                    reporte.setIdUsuario(rs.getInt("idUsuario"));
                    reporte.setTitulo(rs.getString("titulo"));
                    reporte.setDescripcion(rs.getString("descripcion"));

                    // Convertir Timestamp a LocalDateTime
                    Timestamp timestamp = rs.getTimestamp("fecha_reporte");
                    if (timestamp != null) {
                        reporte.setFechaReporte(timestamp.toLocalDateTime());
                    }
                }
            }
        }
        return reporte;
    }

    /**
     * Obtiene todos los reportes de un usuario específico
     * @param idUsuario ID del usuario
     * @return Lista de reportes del usuario
     * @throws SQLException Error en la consulta
     */
    public List<Reporte> findByUsuario(int idUsuario) throws SQLException {
        List<Reporte> reportes = new ArrayList<>();
        String query = "SELECT idReporte_Estado_Ruta, idRuta, idTipo_Reporte, idUsuario, " +
                "titulo, descripcion, fecha_reporte FROM reporte_estado_ruta " +
                "WHERE idUsuario = ? ORDER BY fecha_reporte DESC";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reporte reporte = new Reporte();
                    reporte.setIdReporte(rs.getInt("idReporte_Estado_Ruta"));
                    reporte.setIdRuta(rs.getInt("idRuta"));
                    reporte.setIdTipoReporte(rs.getInt("idTipo_Reporte"));
                    reporte.setIdUsuario(rs.getInt("idUsuario"));
                    reporte.setTitulo(rs.getString("titulo"));
                    reporte.setDescripcion(rs.getString("descripcion"));

                    Timestamp timestamp = rs.getTimestamp("fecha_reporte");
                    if (timestamp != null) {
                        reporte.setFechaReporte(timestamp.toLocalDateTime());
                    }

                    reportes.add(reporte);
                }
            }
        }
        return reportes;
    }

    /**
     * Guarda un nuevo reporte en la base de datos
     * @param reporte Reporte a guardar
     * @return ID del reporte creado
     * @throws SQLException Error en la inserción
     */
    public int save(Reporte reporte) throws SQLException {
        String query = "INSERT INTO reporte_estado_ruta (idRuta, idTipo_Reporte, idUsuario, titulo, descripcion) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reporte.getIdRuta());
            stmt.setInt(2, reporte.getIdTipoReporte());
            stmt.setInt(3, reporte.getIdUsuario());
            stmt.setString(4, reporte.getTitulo());
            stmt.setString(5, reporte.getDescripcion());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Error al crear reporte, no se insertaron filas");
            }

            // Obtener el ID generado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al crear reporte, no se obtuvo el ID");
                }
            }
        }
    }

    /**
     * Actualiza un reporte existente
     * @param reporte Reporte con los datos actualizados
     * @return true si se actualizó correctamente, false si no se encontró el reporte
     * @throws SQLException Error en la actualización
     */
    public boolean update(Reporte reporte) throws SQLException {
        String query = "UPDATE reporte_estado_ruta SET idRuta = ?, idTipo_Reporte = ?, " +
                "titulo = ?, descripcion = ? WHERE idReporte_Estado_Ruta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reporte.getIdRuta());
            stmt.setInt(2, reporte.getIdTipoReporte());
            stmt.setString(3, reporte.getTitulo());
            stmt.setString(4, reporte.getDescripcion());
            stmt.setInt(5, reporte.getIdReporte());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Elimina un reporte por su ID
     * @param idReporte ID del reporte a eliminar
     * @return true si se eliminó correctamente, false si no se encontró el reporte
     * @throws SQLException Error en la eliminación
     */
    public boolean delete(int idReporte) throws SQLException {
        String query = "DELETE FROM reporte_estado_ruta WHERE idReporte_Estado_Ruta = ?";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idReporte);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Obtiene el conteo total de reportes
     * @return Número total de reportes
     * @throws SQLException Error en la consulta
     */
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) FROM reporte_estado_ruta";

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}