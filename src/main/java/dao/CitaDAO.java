package dao;

import dto.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaDAO {

    boolean insertar(Cita cita, int idUsuario, String ip);

    boolean actualizar(Cita cita, int idUsuario, String ip);

    boolean eliminar(int id, int idUsuario, String ip);

    Cita obtenerPorId(int id);

    List<Cita> obtenerTodas();

    List<Cita> obtenerPorPaciente(int idPaciente);

    List<Cita> obtenerPorMedico(int idMedico);

    List<Cita> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin);

    List<Cita> obtenerConFiltros(String buscarPaciente, Integer idMedico, String estado, LocalDate fechaInicio, LocalDate fechaFin);

    int contarCitas();

    int contarCitasPorEstado(String estado);

    int contarCitasHoy();

    List<Cita> obtenerUltimasCitas(int limite);

    List<Cita> obtenerPorDocumentoPaciente(String documento);

    boolean existeCitaEnHorario(int idMedico, LocalDate fecha, LocalTime hora);

    boolean existeCitaEnHorarioExcepto(int idMedico, LocalDate fecha, LocalTime hora, int idCita);

    boolean existeCitaCercana(int idMedico, LocalDate fecha, LocalTime hora, int minutos);
    
    boolean existeCitaCercanaExcepto(int idMedico, LocalDate fecha, LocalTime hora, int minutos, int idCita);
}
