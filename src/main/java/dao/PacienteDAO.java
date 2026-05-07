package dao;

import dto.Paciente;
import java.util.List;

public interface PacienteDAO {

    boolean insertar(Paciente paciente);

    boolean actualizar(Paciente paciente);

    boolean eliminar(int id);

    Paciente obtenerPorId(int id);

    Paciente obtenerPorDocumento(String documento);

    List<Paciente> obtenerTodos();

    List<Paciente> buscarPorNombre(String texto);

    int contarPacientes();

}
