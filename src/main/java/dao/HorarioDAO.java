package dao;

import dto.Horario;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface HorarioDAO {

    boolean insertar(Horario horario, int idUsuario, String ip);

    boolean actualizar(Horario horario, int idUsuario, String ip);

    boolean eliminar(int id, int idUsuario, String ip);

    Horario obtenerPorId(int id);

    List<Horario> obtenerPorMedico(int idMedico);

    List<Horario> obtenerTodos();

    int contarHorarios();

    boolean esHorarioValido(int idMedico, LocalDate fecha, LocalTime hora);

    boolean isHorarioLaboral(int idMedico, LocalDate fecha, LocalTime hora);
}
