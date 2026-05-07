package dao;

import dto.Horario;
import dto.Usuario;
import model.Conexion;
import util.AuditService;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HorarioImpl implements HorarioDAO {

    // Método auxiliar para obtener username a partir de ID (puedes implementarlo con un UsuarioDAO)
    private String obtenerUsername(int idUsuario) {
        if (idUsuario <= 0) {
            return "SISTEMA";
        }
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT username FROM usuarios WHERE id = ?")) {
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
    public boolean insertar(Horario horario, int idUsuario, String ip) {
        String sql = "INSERT INTO horarios (id_medico, dia_semana, hora_inicio, hora_fin, max_citas) VALUES (?,?,?,?,?)";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, horario.getIdMedico());
            stmt.setInt(2, horario.getDiaSemana());
            stmt.setTime(3, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(4, Time.valueOf(horario.getHoraFin()));
            stmt.setInt(5, horario.getMaxCitas());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        horario.setId(rs.getInt(1));
                    }
                }
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "INSERT_HORARIO", ip, "EXITO");
                return true;
            }
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "INSERT_HORARIO", ip, "FALLO");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean actualizar(Horario horario, int idUsuario, String ip) {
        String sql = "UPDATE horarios SET id_medico=?, dia_semana=?, hora_inicio=?, hora_fin=?, max_citas=? WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, horario.getIdMedico());
            stmt.setInt(2, horario.getDiaSemana());
            stmt.setTime(3, Time.valueOf(horario.getHoraInicio()));
            stmt.setTime(4, Time.valueOf(horario.getHoraFin()));
            stmt.setInt(5, horario.getMaxCitas());
            stmt.setInt(6, horario.getId());
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_HORARIO", ip, "EXITO");
            } else {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_HORARIO", ip, "FALLO");
            }
            return ok;
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_HORARIO", ip, "FALLO");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id, int idUsuario, String ip) {
        String sql = "DELETE FROM horarios WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_HORARIO", ip, "EXITO");
            } else {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_HORARIO", ip, "FALLO");
            }
            return ok;
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_HORARIO", ip, "FALLO");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Horario obtenerPorId(int id) {
        String sql = "SELECT * FROM horarios WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearHorario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Horario> obtenerPorMedico(int idMedico) {
        List<Horario> lista = new ArrayList<>();
        String sql = "SELECT * FROM horarios WHERE id_medico=? ORDER BY dia_semana, hora_inicio";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearHorario(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Horario> obtenerTodos() {
        List<Horario> lista = new ArrayList<>();
        String sql = "SELECT * FROM horarios ORDER BY id_medico, dia_semana, hora_inicio";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearHorario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public int contarHorarios() {
        String sql = "SELECT COUNT(*) FROM horarios";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean esHorarioValido(int idMedico, LocalDate fecha, LocalTime hora) {
        // Obtener el día de la semana (1=Lunes, 7=Domingo)
        int diaSemana = fecha.getDayOfWeek().getValue();
        // Convertir: Lunes=1, Domingo=7 (coincide con tu tabla)

        String sql = "SELECT hora_inicio, hora_fin FROM horarios WHERE id_medico = ? AND dia_semana = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setInt(2, diaSemana);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LocalTime inicio = rs.getTime("hora_inicio").toLocalTime();
                    LocalTime fin = rs.getTime("hora_fin").toLocalTime();
                    return !hora.isBefore(inicio) && !hora.isAfter(fin);
                } else {
                    // Si no hay horario definido para ese médico ese día, no permitir cita
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isHorarioLaboral(int idMedico, LocalDate fecha, LocalTime hora) {
        // Obtener número de día de la semana (1=lunes, 2=martes, ..., 7=domingo)
        int diaSemana = fecha.getDayOfWeek().getValue();
        String sql = "SELECT COUNT(*) FROM horarios WHERE id_medico = ? AND dia_semana = ? AND hora_inicio <= ? AND hora_fin >= ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setInt(2, diaSemana);
            stmt.setTime(3, Time.valueOf(hora));
            stmt.setTime(4, Time.valueOf(hora));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Horario mapearHorario(ResultSet rs) throws SQLException {
        Horario h = new Horario();
        h.setId(rs.getInt("id"));
        h.setIdMedico(rs.getInt("id_medico"));
        h.setDiaSemana(rs.getInt("dia_semana"));
        Time hi = rs.getTime("hora_inicio");
        if (hi != null) {
            h.setHoraInicio(hi.toLocalTime());
        }
        Time hf = rs.getTime("hora_fin");
        if (hf != null) {
            h.setHoraFin(hf.toLocalTime());
        }
        h.setMaxCitas(rs.getInt("max_citas"));
        return h;
    }
}
