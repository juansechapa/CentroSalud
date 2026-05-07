package dao;

import dto.LogAcceso;
import model.Conexion;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogAccesoImpl implements LogAccesoDAO {

    @Override
    public boolean insertar(LogAcceso log) {
        String sql = "INSERT INTO log_accesos (id_usuario, username, accion, ip, resultado, fecha) VALUES (?,?,?,?,?,?)";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, log.getIdUsuario());
            stmt.setString(2, log.getUsername());
            stmt.setString(3, log.getAccion());
            stmt.setString(4, log.getIp());
            stmt.setString(5, log.getResultado());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        log.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<LogAcceso> obtenerPorUsuario(int idUsuario) {
        List<LogAcceso> lista = new ArrayList<>();
        String sql = "SELECT * FROM log_accesos WHERE id_usuario = ? ORDER BY fecha DESC";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<LogAcceso> obtenerPorAccion(String accion) {
        List<LogAcceso> lista = new ArrayList<>();
        String sql = "SELECT * FROM log_accesos WHERE accion = ? ORDER BY fecha DESC";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accion);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<LogAcceso> obtenerTodos() {
        List<LogAcceso> lista = new ArrayList<>();
        String sql = "SELECT * FROM log_accesos ORDER BY fecha DESC";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private LogAcceso mapearLog(ResultSet rs) throws SQLException {
        LogAcceso log = new LogAcceso();
        log.setId(rs.getInt("id"));
        log.setIdUsuario(rs.getInt("id_usuario"));
        log.setUsername(rs.getString("username"));
        log.setAccion(rs.getString("accion"));
        log.setIp(rs.getString("ip"));
        log.setResultado(rs.getString("resultado"));
        log.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        return log;
    }
}
