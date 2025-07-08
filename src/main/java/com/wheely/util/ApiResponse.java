package com.wheely.util;

/**
 * Clase para estandarizar las respuestas de la API
 * Proporciona un formato consistente para respuestas exitosas y de error
 */
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    // Constructor vacío
    public ApiResponse() {}

    // Constructor completo
    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor para respuestas exitosas con datos
    public ApiResponse(String message, Object data) {
        this.success = true;
        this.message = message;
        this.data = data;
    }

    // Constructor para respuestas exitosas sin datos
    public ApiResponse(String message) {
        this.success = true;
        this.message = message;
        this.data = null;
    }

    // Métodos estáticos para crear respuestas comunes
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }

    public static ApiResponse error(String message, Object errorDetails) {
        return new ApiResponse(false, message, errorDetails);
    }

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}