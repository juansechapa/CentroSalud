package dao;

import dto.Cita;
import model.Conexion;
import util.AuditService;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaImpl implements CitaDAO {

    // ================== MÉTODOS CRUD ==================
    @Override
    public boolean insertar(Cita cita, int idUsuario, String ip) {
        String sql = "INSERT INTO citas (id_paciente, id_medico, id_especialidad, fecha_cita, hora_cita, motivo, estado, observaciones, fecha_registro, id_registrado_por) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cita.getIdPaciente());
            stmt.setInt(2, cita.getIdMedico());
            stmt.setInt(3, cita.getIdEspecialidad());
            stmt.setDate(4, Date.valueOf(cita.getFechaCita()));
            stmt.setTime(5, Time.valueOf(cita.getHoraCita()));
            stmt.setString(6, cita.getMotivo());
            stmt.setString(7, cita.getEstado());
            stmt.setString(8, cita.getObservaciones());
            if (cita.getIdRegistradoPor() != null) {
                stmt.setInt(9, cita.getIdRegistradoPor());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cita.setId(rs.getInt(1));
                    }
                }
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "INSERT_CITA", ip, "EXITO");
                return true;
            } else {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "INSERT_CITA", ip, "FALLO");
                return false;
            }
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "INSERT_CITA", ip, "FALLO");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizar(Cita cita, int idUsuario, String ip) {
        String sql = "UPDATE citas SET id_paciente=?, id_medico=?, id_especialidad=?, fecha_cita=?, hora_cita=?, motivo=?, estado=?, observaciones=? WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cita.getIdPaciente());
            stmt.setInt(2, cita.getIdMedico());
            stmt.setInt(3, cita.getIdEspecialidad());
            stmt.setDate(4, Date.valueOf(cita.getFechaCita()));
            stmt.setTime(5, Time.valueOf(cita.getHoraCita()));
            stmt.setString(6, cita.getMotivo());
            stmt.setString(7, cita.getEstado());
            stmt.setString(8, cita.getObservaciones());
            stmt.setInt(9, cita.getId());
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_CITA", ip, "EXITO");
            } else {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_CITA", ip, "FALLO");
            }
            return ok;
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "UPDATE_CITA", ip, "FALLO");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id, int idUsuario, String ip) {
        String sql = "DELETE FROM citas WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_CITA", ip, "EXITO");
            } else {
                AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_CITA", ip, "FALLO");
            }
            return ok;
        } catch (SQLException e) {
            AuditService.registrar(idUsuario, obtenerUsername(idUsuario), "DELETE_CITA", ip, "FALLO");
            e.printStackTrace();
            return false;
        }
    }

    // ================== CONSULTAS CON NOMBRES (incluyen documento del paciente) ==================
    @Override
    public Cita obtenerPorId(int id) {
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.documento AS p_documento, "
                + "u.nombres AS m_nom, u.apellidos AS m_ape, e.nombre AS esp_nombre "
                + "FROM citas c "
                + "JOIN pacientes p ON c.id_paciente = p.id "
                + "JOIN usuarios u ON c.id_medico = u.id "
                + "JOIN especialidades e ON c.id_especialidad = e.id "
                + "WHERE c.id = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearConNombres(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Cita> obtenerTodas() {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.documento AS p_documento, "
                + "u.nombres AS m_nom, u.apellidos AS m_ape, e.nombre AS esp_nombre "
                + "FROM citas c "
                + "JOIN pacientes p ON c.id_paciente = p.id "
                + "JOIN usuarios u ON c.id_medico = u.id "
                + "JOIN especialidades e ON c.id_especialidad = e.id "
                + "ORDER BY c.fecha_cita DESC, c.hora_cita";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearConNombres(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> obtenerPorPaciente(int idPaciente) {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.documento AS p_documento, "
                + "u.nombres AS m_nom, u.apellidos AS m_ape, e.nombre AS esp_nombre "
                + "FROM citas c "
                + "JOIN pacientes p ON c.id_paciente = p.id "
                + "JOIN usuarios u ON c.id_medico = u.id "
                + "JOIN especialidades e ON c.id_especialidad = e.id "
                + "WHERE c.id_paciente = ? ORDER BY c.fecha_cita DESC, c.hora_cita";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPaciente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearConNombres(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> obtenerPorMedico(int idMedico) {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.documento AS p_documento, "
                + "u.nombres AS m_nom, u.apellidos AS m_ape, e.nombre AS esp_nombre "
                + "FROM citas c "
                + "JOIN pacientes p ON c.id_paciente = p.id "
                + "JOIN usuarios u ON c.id_medico = u.id "
                + "JOIN especialidades e ON c.id_especialidad = e.id "
                + "WHERE c.id_medico = ? ORDER BY c.fecha_cita DESC, c.hora_cita";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearConNombres(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin) {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.documento AS p_documento, "
                + "u.nombres AS m_nom, u.apellidos AS m_ape, e.nombre AS esp_nombre "
                + "FROM citas c "
                + "JOIN pacientes p ON c.id_paciente = p.id "
                + "JOIN usuarios u ON c.id_medico = u.id "
                + "JOIN especialidades e ON c.id_especialidad = e.id "
                + "WHERE c.fecha_cita BETWEEN ? AND ? ORDER BY c.fecha_cita, c.hora_cita";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fin));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearConNombres(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> obtenerConFiltros(String buscarPaciente, Integer idMedico, String estado,
            LocalDate fechaInicio, LocalDate fechaFin) {
        List<Cita> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.documento AS p_documento, "
                + "u.nombres AS m_nom, u.apellidos AS m_ape, e.nombre AS esp_nombre "
                + "FROM citas c "
                + "JOIN pacientes p ON c.id_paciente = p.id "
                + "JOIN usuarios u ON c.id_medico = u.id "
                + "JOIN especialidades e ON c.id_especialidad = e.id WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();
        if (buscarPaciente != null && !buscarPaciente.trim().isEmpty()) {
            sql.append(" AND (p.nombres LIKE ? OR p.apellidos LIKE ? OR p.documento LIKE ?)");
            String like = "%" + buscarPaciente + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (idMedico != null && idMedico > 0) {
            sql.append(" AND c.id_medico = ?");
            params.add(idMedico);
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND c.estado = ?");
            params.add(estado);
        }
        if (fechaInicio != null) {
            sql.append(" AND c.fecha_cita >= ?");
            params.add(fechaInicio);
        }
        if (fechaFin != null) {
            sql.append(" AND c.fecha_cita <= ?");
            params.add(fechaFin);
        }
        sql.append(" ORDER BY c.fecha_cita DESC, c.hora_cita");
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i) instanceof String) {
                    stmt.setString(i + 1, (String) params.get(i));
                } else if (params.get(i) instanceof LocalDate) {
                    stmt.setDate(i + 1, Date.valueOf((LocalDate) params.get(i)));
                } else if (params.get(i) instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) params.get(i));
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearConNombres(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> obtenerUltimasCitas(int limite) {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.documento AS p_documento, "
                + "u.nombres AS m_nom, u.apellidos AS m_ape, e.nombre AS esp_nombre "
                + "FROM citas c "
                + "JOIN pacientes p ON c.id_paciente = p.id "
                + "JOIN usuarios u ON c.id_medico = u.id "
                + "JOIN especialidades e ON c.id_especialidad = e.id "
                + "ORDER BY c.fecha_cita DESC, c.hora_cita DESC LIMIT ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearConNombres(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Cita> obtenerPorDocumentoPaciente(String documento) {
        List<Cita> lista = new ArrayList<>();
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.documento AS p_documento, "
                + "u.nombres AS m_nom, u.apellidos AS m_ape, e.nombre AS esp_nombre "
                + "FROM citas c "
                + "JOIN pacientes p ON c.id_paciente = p.id "
                + "JOIN usuarios u ON c.id_medico = u.id "
                + "JOIN especialidades e ON c.id_especialidad = e.id "
                + "WHERE p.documento = ? "
                + "ORDER BY c.fecha_cita DESC, c.hora_cita";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearConNombres(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ================== VALIDACIONES ==================
    @Override
    public boolean existeCitaEnHorario(int idMedico, LocalDate fecha, LocalTime hora) {
        String sql = "SELECT COUNT(*) FROM citas WHERE id_medico = ? AND fecha_cita = ? AND hora_cita = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setDate(2, Date.valueOf(fecha));
            stmt.setTime(3, Time.valueOf(hora));
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

    @Override
    public boolean existeCitaEnHorarioExcepto(int idMedico, LocalDate fecha, LocalTime hora, int idCita) {
        String sql = "SELECT COUNT(*) FROM citas WHERE id_medico = ? AND fecha_cita = ? AND hora_cita = ? AND id != ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setDate(2, Date.valueOf(fecha));
            stmt.setTime(3, Time.valueOf(hora));
            stmt.setInt(4, idCita);
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

    @Override
    public boolean existeCitaCercana(int idMedico, LocalDate fecha, LocalTime hora, int minutos) {
        LocalTime inicioRango = hora.minusMinutes(minutos);
        LocalTime finRango = hora.plusMinutes(minutos);
        String sql = "SELECT COUNT(*) FROM citas WHERE id_medico = ? AND fecha_cita = ? AND hora_cita BETWEEN ? AND ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setDate(2, Date.valueOf(fecha));
            stmt.setTime(3, Time.valueOf(inicioRango));
            stmt.setTime(4, Time.valueOf(finRango));
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

    @Override
    public boolean existeCitaCercanaExcepto(int idMedico, LocalDate fecha, LocalTime hora, int minutos, int idCita) {
        LocalTime inicioRango = hora.minusMinutes(minutos);
        LocalTime finRango = hora.plusMinutes(minutos);
        String sql = "SELECT COUNT(*) FROM citas WHERE id_medico = ? AND fecha_cita = ? AND hora_cita BETWEEN ? AND ? AND id != ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMedico);
            stmt.setDate(2, Date.valueOf(fecha));
            stmt.setTime(3, Time.valueOf(inicioRango));
            stmt.setTime(4, Time.valueOf(finRango));
            stmt.setInt(5, idCita);
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

    // ================== ESTADÍSTICAS ==================
    @Override
    public int contarCitas() {
        String sql = "SELECT COUNT(*) FROM citas";
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
    public int contarCitasPorEstado(String estado) {
        String sql = "SELECT COUNT(*) FROM citas WHERE estado = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
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

    @Override
    public int contarCitasHoy() {
        String sql = "SELECT COUNT(*) FROM citas WHERE fecha_cita = CURDATE()";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ================== AUXILIARES ==================
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

    private Cita mapearConNombres(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setId(rs.getInt("id"));
        c.setIdPaciente(rs.getInt("id_paciente"));
        c.setIdMedico(rs.getInt("id_medico"));
        c.setIdEspecialidad(rs.getInt("id_especialidad"));
        Date fecha = rs.getDate("fecha_cita");
        if (fecha != null) {
            c.setFechaCita(fecha.toLocalDate());
        }
        Time hora = rs.getTime("hora_cita");
        if (hora != null) {
            c.setHoraCita(hora.toLocalTime());
        }
        c.setMotivo(rs.getString("motivo"));
        c.setEstado(rs.getString("estado"));
        c.setObservaciones(rs.getString("observaciones"));
        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            c.setFechaRegistro(ts.toLocalDateTime());
        }
        int idReg = rs.getInt("id_registrado_por");
        if (!rs.wasNull()) {
            c.setIdRegistradoPor(idReg);
        }

        c.setNombrePaciente(rs.getString("p_nom") + " " + rs.getString("p_ape"));
        c.setNombreMedico(rs.getString("m_nom") + " " + rs.getString("m_ape"));
        c.setNombreEspecialidad(rs.getString("esp_nombre"));
        c.setDocumentoPaciente(rs.getString("p_documento"));
        return c;
    }
}
