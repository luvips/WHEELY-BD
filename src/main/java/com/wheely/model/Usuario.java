package com.wheely.model;

/**
 * Modelo de datos para la tabla usuario
 * Representa a un usuario del sistema Wheely
 */
public class Usuario {
    private int idUser;
    private String nombre;
    private String email;
    private String password;

    // Constructor vacío requerido para Jackson (serialización JSON)
    public Usuario() {}

    // Constructor completo
    public Usuario(int idUser, String nombre, String email, String password) {
        this.idUser = idUser;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    // Constructor sin ID (para creación de nuevos usuarios)
    public Usuario(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // toString para debugging
    @Override
    public String toString() {
        return "Usuario{" +
                "idUser=" + idUser +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}