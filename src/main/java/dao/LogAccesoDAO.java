package dao;

import dto.LogAcceso;
import java.util.List;

public interface LogAccesoDAO {

    boolean insertar(LogAcceso log);

    List<LogAcceso> obtenerPorUsuario(int idUsuario);

    List<LogAcceso> obtenerPorAccion(String accion);

    List<LogAcceso> obtenerTodos();

}
