package servlet;

import dto.Horario;
import dto.Usuario;
import dao.HorarioDAO;
import dao.HorarioImpl;
import dao.UsuarioDAO;
import dao.UsuarioImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/HorarioServlet")
public class HorarioServlet extends HttpServlet {

    private HorarioDAO horarioDAO = new HorarioImpl();
    private UsuarioDAO usuarioDAO = new UsuarioImpl();

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
                listarHorarios(request, response);
                break;
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "editar":
                mostrarFormularioEditar(request, response);
                break;
            case "eliminar":
                eliminarHorario(request, response);
                break;
            default:
                listarHorarios(request, response);
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
                insertarHorario(request, response);
                break;
            case "actualizar":
                actualizarHorario(request, response);
                break;
            default:
                listarHorarios(request, response);
        }
    }

    private void listarHorarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Horario> horarios = horarioDAO.obtenerTodos();
        request.setAttribute("listaHorarios", horarios);
        request.getRequestDispatcher("/views/horarios/lista.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("personal", usuarioDAO.obtenerPersonalActivo());
        request.getRequestDispatcher("/views/horarios/formulario.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("personal", usuarioDAO.obtenerPersonalActivo());
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            Horario horario = horarioDAO.obtenerPorId(id);
            if (horario == null) {
                request.setAttribute("error", "Horario no encontrado");
                listarHorarios(request, response);
                return;
            }
            request.setAttribute("horario", horario);
            request.getRequestDispatcher("/views/horarios/formulario.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar");
        }
    }

    private void eliminarHorario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            HttpSession session = request.getSession(false);
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();  // Obtener IP
            boolean exito = horarioDAO.eliminar(id, usuario.getId(), ip);  // ← tercer parámetro
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar&mensaje=Horario eliminado correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar&error=No se pudo eliminar el horario");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar");
        }
    }

    private void insertarHorario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idMedicoStr = request.getParameter("idMedico");
        String diaSemanaStr = request.getParameter("diaSemana");
        String horaInicio = request.getParameter("horaInicio");
        String horaFin = request.getParameter("horaFin");
        String maxCitasStr = request.getParameter("maxCitas");

        if (idMedicoStr == null || diaSemanaStr == null || horaInicio == null || horaFin == null || maxCitasStr == null) {
            response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=nuevo&error=Datos incompletos");
            return;
        }
        try {
            Horario h = new Horario();
            h.setIdMedico(Integer.parseInt(idMedicoStr));
            h.setDiaSemana(Integer.parseInt(diaSemanaStr));
            h.setHoraInicio(LocalTime.parse(horaInicio));
            h.setHoraFin(LocalTime.parse(horaFin));
            h.setMaxCitas(Integer.parseInt(maxCitasStr));

            HttpSession session = request.getSession(false);
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();  // Obtener IP
            boolean exito = horarioDAO.insertar(h, usuario.getId(), ip);  // ← tercer parámetro
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar&mensaje=Horario registrado correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=nuevo&error=Error al guardar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=nuevo&error=Datos inválidos");
        }
    }

    private void actualizarHorario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getParameter("id");
        String idMedicoStr = request.getParameter("idMedico");
        String diaSemanaStr = request.getParameter("diaSemana");
        String horaInicio = request.getParameter("horaInicio");
        String horaFin = request.getParameter("horaFin");
        String maxCitasStr = request.getParameter("maxCitas");

        if (idStr == null || idMedicoStr == null || diaSemanaStr == null || horaInicio == null || horaFin == null || maxCitasStr == null) {
            response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar&error=Datos incompletos");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            Horario h = new Horario();
            h.setId(id);
            h.setIdMedico(Integer.parseInt(idMedicoStr));
            h.setDiaSemana(Integer.parseInt(diaSemanaStr));
            h.setHoraInicio(LocalTime.parse(horaInicio));
            h.setHoraFin(LocalTime.parse(horaFin));
            h.setMaxCitas(Integer.parseInt(maxCitasStr));

            HttpSession session = request.getSession(false);
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();  // Obtener IP
            boolean exito = horarioDAO.actualizar(h, usuario.getId(), ip);  // ← tercer parámetro
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar&mensaje=Horario actualizado correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=editar&id=" + id + "&error=Error al actualizar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/HorarioServlet?accion=listar&error=Datos inválidos");
        }
    }
}
