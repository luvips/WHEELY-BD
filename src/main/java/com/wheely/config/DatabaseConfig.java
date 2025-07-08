package com.wheely.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;

/**
 * Configuración de la conexión a la base de datos MySQL
 */
public class DatabaseConfig {
    private static HikariDataSource dataSource;

    /**
     * Obtiene la fuente de datos configurada
     * Implementa patrón Singleton para reutilizar la conexión
     * @return DataSource configurado con las credenciales del archivo .env
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            // Cargar variables de entorno desde archivo .env
            Dotenv dotenv = Dotenv.load();

            // Obtener configuración de base de datos
            String host = dotenv.get("DB_HOST");
            String dbName = dotenv.get("DB_SCHEMA");
            String jdbcUrl = String.format("jdbc:mysql://%s:3306/%s", host, dbName);

            // Configurar HikariCP (pool de conexiones)
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dotenv.get("DB_USER"));
            config.setPassword(dotenv.get("DB_PASS"));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Configuraciones adicionales para optimizar el pool
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    /**
     * Cierra el pool de conexiones
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}