package com.wheely.service;

import com.wheely.model.Usuario;
import com.wheely.repository.UsuarioRepository;
import com.wheely.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para la lógica de negocio de usuarios
 * Contiene validaciones y manejo de contraseñas
 */
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene todos los usuarios del sistema
     * @return Lista de usuarios sin contraseñas
     * @throws SQLException Error en la consulta
     */
    public List<Usuario> getAllUsuarios() throws SQLException {
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Limpiar contraseñas por seguridad antes de retornar
        usuarios.forEach(usuario -> usuario.setPassword(""));
        return usuarios;
    }

    /**
     * Busca un usuario por su ID
     * @param idUser ID del usuario
     * @return Usuario encontrado sin contraseña
     * @throws SQLException Error en la consulta
     */
    public Usuario getUsuarioById(int idUser) throws SQLException {
        Usuario usuario = usuarioRepository.findById(idUser);
        if (usuario != null) {
            // Limpiar contraseña por seguridad
            usuario.setPassword("");
        }
        return usuario;
    }

    /**
     * Crea un nuevo usuario con contraseña hasheada
     * @param usuario Usuario a crear
     * @return ID del usuario creado
     * @throws SQLException Error en la base de datos
     * @throws IllegalArgumentException Error de validación
     */
    public int createUsuario(Usuario usuario) throws SQLException {
        // Validar datos del usuario
        validateUsuario(usuario);

        // Verificar si el email ya existe
        if (usuarioRepository.emailExists(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validar contraseña
        if (!PasswordUtil.isValidPassword(usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }

        // Hashear la contraseña antes de guardar
        String hashedPassword = PasswordUtil.hashPassword(usuario.getPassword());
        usuario.setPassword(hashedPassword);

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza un usuario existente
     * @param usuario Usuario con datos actualizados
     * @return true si se actualizó correctamente
     * @throws SQLException Error en la base de datos
     * @throws IllegalArgumentException Error de validación
     */
    public boolean updateUsuario(Usuario usuario) throws SQLException {
        // Validar que el usuario existe
        Usuario usuarioExistente = usuarioRepository.findById(usuario.getIdUser());
        if (usuarioExistente == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Validar datos del usuario
        validateUsuario(usuario);

        // Verificar si el email ya existe en otro usuario
        Usuario usuarioConEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioConEmail != null && usuarioConEmail.getIdUser() != usuario.getIdUser()) {
            throw new IllegalArgumentException("El email ya está registrado en otro usuario");
        }

        // Si se proporciona una nueva contraseña, validar y hashear
        if (usuario.getPassword() != null && !usuario.getPassword().trim().isEmpty()) {
            if (!PasswordUtil.isValidPassword(usuario.getPassword())) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }
            String hashedPassword = PasswordUtil.hashPassword(usuario.getPassword());
            usuario.setPassword(hashedPassword);
        } else {
            // Mantener la contraseña existente
            usuario.setPassword(usuarioExistente.getPassword());
        }

        return usuarioRepository.update(usuario);
    }

    /**
     * Elimina un usuario por su ID
     * @param idUser ID del usuario a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException Error en la base de datos
     */
    public boolean deleteUsuario(int idUser) throws SQLException {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(idUser);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        return usuarioRepository.delete(idUser);
    }

    /**
     * Autentica un usuario con email y contraseña
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return Usuario autenticado sin contraseña, o null si las credenciales son incorrectas
     * @throws SQLException Error en la base de datos
     */
    public Usuario authenticateUsuario(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty() || password == null) {
            return null;
        }

        Usuario usuario = usuarioRepository.findByEmail(email.trim());
        if (usuario == null) {
            return null;
        }

        // Verificar contraseña
        if (PasswordUtil.verifyPassword(password, usuario.getPassword())) {
            // Limpiar contraseña antes de retornar
            usuario.setPassword("");
            return usuario;
        }

        return null;
    }

    /**
     * Cambia la contraseña de un usuario
     * @param idUser ID del usuario
     * @param currentPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return true si se cambió correctamente
     * @throws SQLException Error en la base de datos
     * @throws IllegalArgumentException Error de validación
     */
    public boolean changePassword(int idUser, String currentPassword, String newPassword) throws SQLException {
        Usuario usuario = usuarioRepository.findById(idUser);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Verificar contraseña actual
        if (!PasswordUtil.verifyPassword(currentPassword, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        // Validar nueva contraseña
        if (!PasswordUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        // Hashear y actualizar nueva contraseña
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        usuario.setPassword(hashedPassword);

        return usuarioRepository.update(usuario);
    }

    /**
     * Valida los datos básicos de un usuario
     * @param usuario Usuario a validar
     * @throws IllegalArgumentException Si los datos no son válidos
     */
    private void validateUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Los datos del usuario son requeridos");
        }

        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es requerido");
        }

        // Validar formato de email básico
        if (!isValidEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        // Validar longitud del nombre
        if (usuario.getNombre().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder 100 caracteres");
        }

        // Validar longitud del email
        if (usuario.getEmail().trim().length() > 100) {
            throw new IllegalArgumentException("El email no puede exceder 100 caracteres");
        }
    }

    /**
     * Valida el formato básico de un email
     * @param email Email a validar
     * @return true si el formato es válido
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}