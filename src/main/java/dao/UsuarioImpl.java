package dao;

import dto.Usuario;
import model.Conexion;
import util.AuditService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioImpl implements UsuarioDAO {

    @Override
    public boolean insertar(Usuario usuario, int idUsuario, String ip) {
        String sql = "INSERT INTO usuarios (nombres, apellidos, documento, email, username, password, rol, especialidad, lang_preferido, activo) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNombres());
            stmt.setString(2, usuario.getApellidos());
            stmt.setString(3, usuario.getDocumento());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getUsername());
            stmt.setString(6, usuario.getPassword());
            stmt.setString(7, usuario.getRol());
            stmt.setString(8, usuario.getEspecialidad());
            stmt.setString(9, usuario.getLang_preferido());
            stmt.setBoolean(10, usuario.isActivo());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "INSERT_USUARIO", ip, "EXITO");
                return true;
            } else {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "INSERT_USUARIO", ip, "FALLO");
                return false;
            }
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "INSERT_USUARIO", ip, "FALLO");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario usuario, int idUsuario, String ip) {
        String sql = "UPDATE usuarios SET nombres=?, apellidos=?, documento=?, email=?, username=?, password=?, rol=?, especialidad=?, lang_preferido=?, activo=? WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombres());
            stmt.setString(2, usuario.getApellidos());
            stmt.setString(3, usuario.getDocumento());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getUsername());
            stmt.setString(6, usuario.getPassword());
            stmt.setString(7, usuario.getRol());
            stmt.setString(8, usuario.getEspecialidad());
            stmt.setString(9, usuario.getLang_preferido());
            stmt.setBoolean(10, usuario.isActivo());
            stmt.setInt(11, usuario.getId());
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_USUARIO", ip, "EXITO");
            } else {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_USUARIO", ip, "FALLO");
            }
            return ok;
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_USUARIO", ip, "FALLO");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id, int idUsuario, String ip) {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_USUARIO", ip, "EXITO");
            } else {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_USUARIO", ip, "FALLO");
            }
            return ok;
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_USUARIO", ip, "FALLO");
            e.printStackTrace();
            return false;
        }
    }

    // Los métodos de consulta se mantienen igual (sin auditoría)
    @Override
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Usuario obtenerPorUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Usuario> obtenerPorRol(String rol) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol=? ORDER BY apellidos, nombres";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rol);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY apellidos, nombres";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Usuario> obtenerPersonalActivo() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol IN ('MEDICO', 'ENFERMERO', 'RECEPCIONISTA') AND activo = 1 ORDER BY apellidos, nombres";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private String obtenerUsername(int idUsuario) {
        if (idUsuario <= 0) {
            return "SISTEMA";
        }
        String sql = "SELECT username FROM usuarios WHERE id = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "DESCONOCIDO";
    }

    @Override
    public int contarUsuariosPorRol(String rol) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE rol = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rol);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombres(rs.getString("nombres"));
        u.setApellidos(rs.getString("apellidos"));
        u.setDocumento(rs.getString("documento"));
        u.setEmail(rs.getString("email"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));
        u.setEspecialidad(rs.getString("especialidad"));
        u.setLang_preferido(rs.getString("lang_preferido"));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}
