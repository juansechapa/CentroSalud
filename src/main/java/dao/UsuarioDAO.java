package dao;

import dto.Usuario;
import java.util.List;

public interface UsuarioDAO {

    boolean insertar(Usuario usuario, int idUsuario, String ip);

    boolean actualizar(Usuario usuario, int idUsuario, String ip);

    boolean eliminar(int id, int idUsuario, String ip);

    Usuario obtenerPorId(int id);

    Usuario obtenerPorUsername(String username);

    List<Usuario> obtenerPorRol(String rol);

    List<Usuario> obtenerTodos();

    List<Usuario> obtenerPersonalActivo();

    int contarUsuariosPorRol(String rol);
}
