package dao;

import dto.Paciente;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Conexion;

public class PacienteImpl implements PacienteDAO {

    @Override
    public boolean insertar(Paciente paciente) {
        String sql = "INSERT INTO pacientes (nombres, apellidos, documento, fecha_nacimiento, telefono, email, eps, vereda_barrio) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, paciente.getNombres());
            stmt.setString(2, paciente.getApellidos());
            stmt.setString(3, paciente.getDocumento());
            // Manejar fecha nula
            if (paciente.getFechaNacimiento() != null) {
                stmt.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setString(5, paciente.getTelefono());
            stmt.setString(6, paciente.getEmail());
            stmt.setString(7, paciente.getEps());
            stmt.setString(8, paciente.getVeredaBarrio());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        paciente.setId(rs.getInt(1));
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
    public boolean actualizar(Paciente paciente) {
        String sql = "UPDATE pacientes SET nombres=?, apellidos=?, documento=?, fecha_nacimiento=?, telefono=?, email=?, eps=?, vereda_barrio=? WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paciente.getNombres());
            stmt.setString(2, paciente.getApellidos());
            stmt.setString(3, paciente.getDocumento());
            if (paciente.getFechaNacimiento() != null) {
                stmt.setDate(4, Date.valueOf(paciente.getFechaNacimiento()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }
            stmt.setString(5, paciente.getTelefono());
            stmt.setString(6, paciente.getEmail());
            stmt.setString(7, paciente.getEps());
            stmt.setString(8, paciente.getVeredaBarrio());
            stmt.setInt(9, paciente.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM pacientes WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Paciente obtenerPorId(int id) {
        String sql = "SELECT * FROM pacientes WHERE id=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCompleto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Paciente obtenerPorDocumento(String documento) {
        String sql = "SELECT * FROM pacientes WHERE documento=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCompleto(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Paciente> obtenerTodos() {
        List<Paciente> lista = new ArrayList<>();
        // CORREGIDO: SELECT con todas las columnas necesarias (incluyendo fecha_nacimiento)
        String sql = "SELECT id, documento, nombres, apellidos, telefono, email, eps, vereda_barrio, fecha_nacimiento FROM pacientes ORDER BY apellidos";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getInt("id"));
                p.setDocumento(rs.getString("documento"));
                p.setNombres(rs.getString("nombres"));
                p.setApellidos(rs.getString("apellidos"));
                p.setTelefono(rs.getString("telefono"));
                p.setEmail(rs.getString("email"));      // CORREGIDO: antes era "emal"
                p.setEps(rs.getString("eps"));
                p.setVeredaBarrio(rs.getString("vereda_barrio"));
                // Fecha (opcional, puede ser nula)
                Date fecha = rs.getDate("fecha_nacimiento");
                if (fecha != null) {
                    p.setFechaNacimiento(fecha.toLocalDate());
                }
                lista.add(p);  // ← IMPORTANTE: agregar a la lista
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Paciente> buscarPorNombre(String texto) {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT id, documento, nombres, apellidos, telefono, email, eps, vereda_barrio, fecha_nacimiento FROM pacientes WHERE nombres LIKE ? OR apellidos LIKE ? OR documento LIKE ? ORDER BY apellidos";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + texto + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Paciente p = new Paciente();
                    p.setId(rs.getInt("id"));
                    p.setDocumento(rs.getString("documento"));
                    p.setNombres(rs.getString("nombres"));
                    p.setApellidos(rs.getString("apellidos"));
                    p.setTelefono(rs.getString("telefono"));
                    p.setEmail(rs.getString("email"));
                    p.setEps(rs.getString("eps"));
                    p.setVeredaBarrio(rs.getString("vereda_barrio"));
                    Date fecha = rs.getDate("fecha_nacimiento");
                    if (fecha != null) {
                        p.setFechaNacimiento(fecha.toLocalDate());
                    }
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public int contarPacientes() {
        String sql = "SELECT COUNT(*) FROM pacientes";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Método privado para mapear SELECT * (cuando necesitas todas las columnas)
    private Paciente mapearCompleto(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNombres(rs.getString("nombres"));
        p.setApellidos(rs.getString("apellidos"));
        p.setDocumento(rs.getString("documento"));
        Date fecha = rs.getDate("fecha_nacimiento");
        if (fecha != null) {
            p.setFechaNacimiento(fecha.toLocalDate());
        }
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setEps(rs.getString("eps"));
        p.setVeredaBarrio(rs.getString("vereda_barrio"));
        return p;
    }
}
