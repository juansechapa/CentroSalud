package dao;

import dto.Especialidad;
import java.util.List;

public interface EspecialidadDAO {
    List<Especialidad> obtenerTodas();
    Especialidad obtenerPorId(int id);
}