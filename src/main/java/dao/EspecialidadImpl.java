package dao;

import dto.Especialidad;
import model.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadImpl implements EspecialidadDAO {

    @Override
    public List<Especialidad> obtenerTodas() {
        List<Especialidad> lista = new ArrayList<>();
        String sql = "SELECT * FROM especialidades ORDER BY nombre";
        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Especialidad e = new Especialidad();
                e.setId(rs.getInt("id"));
                e.setNombre(rs.getString("nombre"));
                lista.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Especialidad obtenerPorId(int id) {
        String sql = "SELECT * FROM especialidades WHERE id = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Especialidad e = new Especialidad();
                    e.setId(rs.getInt("id"));
                    e.setNombre(rs.getString("nombre"));
                    return e;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
