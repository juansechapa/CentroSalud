package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Valores por defecto para desarrollo local (sin Docker)
    private static final String DEFAULT_HOST = "mysql.railway.internal";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "railway";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "dUJZmnUiztiSgBRCoWrdbcQLcDxNoaQV";

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    private static String getHost() {
        return getEnv("DB_HOST", DEFAULT_HOST);
    }

    private static String getPort() {
        return getEnv("DB_PORT", DEFAULT_PORT);
    }

    private static String getDbName() {
        return getEnv("DB_NAME", DEFAULT_DB);
    }

    private static String getUser() {
        return getEnv("DB_USER", DEFAULT_USER);
    }

    private static String getPassword() {
        return getEnv("DB_PASSWORD", DEFAULT_PASS);
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
