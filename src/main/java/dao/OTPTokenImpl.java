package dao;

import dto.OtpToken;
import model.Conexion;
import java.sql.*;
import java.time.LocalDateTime;

public class OTPTokenImpl implements OTPTokenDAO {

    @Override
    public boolean insertar(OtpToken token) {
        String sql = "INSERT INTO otp_tokens (id_usuario, codigo, fecha_gen, expira_en, usado) VALUES (?,?,?,?,?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, token.getIdUsuario());
            stmt.setString(2, token.getCodigo());
            stmt.setTimestamp(3, Timestamp.valueOf(token.getFechaGen()));
            stmt.setTimestamp(4, Timestamp.valueOf(token.getExpiraEn()));
            stmt.setBoolean(5, token.isUsado());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) token.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public OtpToken obtenerTokenNoUsado(int idUsuario, String codigo) {
        String sql = "SELECT * FROM otp_tokens WHERE id_usuario = ? AND codigo = ? AND usado = 0 AND expira_en > NOW()";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setString(2, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearToken(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean marcarComoUsado(int id) {
        String sql = "UPDATE otp_tokens SET usado = 1 WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void limpiarTokensExpirados() {
        String sql = "DELETE FROM otp_tokens WHERE expira_en < NOW()";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private OtpToken mapearToken(ResultSet rs) throws SQLException {
        OtpToken t = new OtpToken();
        t.setId(rs.getInt("id"));
        t.setIdUsuario(rs.getInt("id_usuario"));
        t.setCodigo(rs.getString("codigo"));
        t.setFechaGen(rs.getTimestamp("fecha_gen").toLocalDateTime());
        t.setExpiraEn(rs.getTimestamp("expira_en").toLocalDateTime());
        t.setUsado(rs.getBoolean("usado"));
        return t;
    }
}