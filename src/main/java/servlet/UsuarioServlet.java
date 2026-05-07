package servlet;

import dao.EspecialidadDAO;
import dao.EspecialidadImpl;
import dto.Usuario;
import dao.UsuarioDAO;
import dao.UsuarioImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioImpl();
    private EspecialidadDAO especialidadDAO = new EspecialidadImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }
        switch (accion) {
            case "listar":
                listarUsuarios(request, response);
                break;
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "editar":
                mostrarFormularioEditar(request, response);
                break;
            case "eliminar":
                eliminarUsuario(request, response);
                break;
            default:
                listarUsuarios(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "insertar";
        }
        switch (accion) {
            case "insertar":
                insertarUsuario(request, response);
                break;
            case "actualizar":
                actualizarUsuario(request, response);
                break;
            default:
                listarUsuarios(request, response);
        }
    }

    private void listarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        request.setAttribute("listaUsuarios", usuarios);
        request.getRequestDispatcher("/views/usuarios/lista.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("especialidades", especialidadDAO.obtenerTodas());
        request.getRequestDispatcher("/views/usuarios/formulario.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            Usuario usuario = usuarioDAO.obtenerPorId(id);
            if (usuario == null) {
                request.setAttribute("error", "Usuario no encontrado");
                listarUsuarios(request, response);
                return;
            }
            request.setAttribute("especialidades", especialidadDAO.obtenerTodas());
            request.setAttribute("empleado", usuario);
            request.getRequestDispatcher("/views/usuarios/formulario.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar");
        }
    }

    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            HttpSession session = request.getSession(false);
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();
            boolean exito = usuarioDAO.eliminar(id, usuarioSesion.getId(), ip);
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar&mensaje=Usuario eliminado correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar&error=No se pudo eliminar el usuario");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar");
        }
    }

    private void insertarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String nombres = request.getParameter("nombres");
            String apellidos = request.getParameter("apellidos");
            String documento = request.getParameter("documento");
            String email = request.getParameter("email");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String rol = request.getParameter("rol");
            String especialidad = request.getParameter("especialidad");
            String lang = request.getParameter("lang_preferido");
            boolean activo = request.getParameter("activo") != null;

            if (nombres == null || nombres.trim().isEmpty()
                    || apellidos == null || apellidos.trim().isEmpty()
                    || username == null || username.trim().isEmpty()
                    || password == null || password.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=nuevo&error=Datos obligatorios faltantes");
                return;
            }
            HttpSession session = request.getSession(false);
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();

            Usuario u = new Usuario();
            u.setNombres(nombres.trim());
            u.setApellidos(apellidos.trim());
            u.setDocumento(documento != null ? documento.trim() : null);
            u.setEmail(email != null ? email.trim() : null);
            u.setUsername(username.trim());
            u.setPassword(password); // IMPORTANTE: en producción debería ser hasheada
            u.setRol(rol);
            u.setEspecialidad(especialidad != null ? especialidad.trim() : null);
            u.setLang_preferido(lang != null ? lang : "es");
            u.setActivo(activo);

            boolean exito = usuarioDAO.insertar(u, usuarioSesion.getId(), ip);
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar&mensaje=Usuario registrado correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=nuevo&error=Error al registrar usuario");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=nuevo&error=Datos inválidos");
        }
    }

    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nombres = request.getParameter("nombres");
            String apellidos = request.getParameter("apellidos");
            String documento = request.getParameter("documento");
            String email = request.getParameter("email");
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String rol = request.getParameter("rol");
            String especialidad = request.getParameter("especialidad");
            String lang = request.getParameter("lang_preferido");
            boolean activo = request.getParameter("activo") != null;

            HttpSession session = request.getSession(false);
            Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();

            Usuario u = new Usuario();
            u.setId(id);
            u.setNombres(nombres);
            u.setApellidos(apellidos);
            u.setDocumento(documento);
            u.setEmail(email);
            u.setUsername(username);
            if (password != null && !password.trim().isEmpty()) {
                u.setPassword(password); // si se envía nueva contraseña, se actualiza
            } else {
                // mantener la existente; mejor obtenerla del objeto original
                Usuario original = usuarioDAO.obtenerPorId(id);
                u.setPassword(original.getPassword());
            }
            u.setRol(rol);
            u.setEspecialidad(especialidad);
            u.setLang_preferido(lang);
            u.setActivo(activo);

            boolean exito = usuarioDAO.actualizar(u, usuarioSesion.getId(), ip);
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar&mensaje=Usuario actualizado correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=editar&id=" + id + "&error=Error al actualizar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=listar&error=Datos inválidos");
        }
    }
}
