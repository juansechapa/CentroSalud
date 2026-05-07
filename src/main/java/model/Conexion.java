package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Valores por defecto para desarrollo local (sin Docker)
    private static final String DEFAULT_HOST = null;
    private static final String DEFAULT_DB = null;
    private static final String DEFAULT_USER = null;
    private static final String DEFAULT_PASS = null;

    private static String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Falta variable de entorno: " + key);
        }
        return value;
    }

    private static String getHost() {
        return requireEnv("DB_HOST");
    }

    private static String getPort() {
        return requireEnv("DB_PORT");
    }

    private static String getDbName() {
        return requireEnv("DB_NAME");
    }

    private static String getUser() {
        return requireEnv("DB_USER");
    }

    private static String getPassword() {
        return requireEnv("DB_PASSWORD");
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDbName()
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver no encontrado", e);
        }
        return DriverManager.getConnection(url, getUser(), getPassword());
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
