package com.wheely.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para el manejo seguro de contraseñas
 * Utiliza BCrypt para el hashing y verificación de contraseñas
 */
public class PasswordUtil {

    // Nivel de complejidad para el hashing (10 es un buen balance entre seguridad y rendimiento)
    private static final int SALT_ROUNDS = 10;

    /**
     * Genera un hash seguro de una contraseña en texto plano
     * @param plainPassword Contraseña en texto plano
     * @return Hash de la contraseña
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash
     * @param plainPassword Contraseña en texto plano a verificar
     * @param hashedPassword Hash almacenado en base de datos
     * @return true si la contraseña coincide, false en caso contrario
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            // Si hay error en la verificación, retornar false por seguridad
            return false;
        }
    }

    /**
     * Valida si una contraseña cumple con criterios mínimos de seguridad
     * @param password Contraseña a validar
     * @return true si la contraseña es válida, false en caso contrario
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }

        // Criterios mínimos: al menos 6 caracteres
        // Puedes agregar más validaciones según tus necesidades
        return password.length() >= 6;
    }
}