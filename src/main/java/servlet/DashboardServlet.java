package servlet;

import dao.*;
import dto.Cita;
import dto.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private PacienteDAO pacienteDAO = new PacienteImpl();
    private CitaDAO citaDAO = new CitaImpl();
    private UsuarioDAO usuarioDAO = new UsuarioImpl();
    private HorarioDAO horarioDAO = new HorarioImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        String rol = (String) session.getAttribute("rol");

        // Estadísticas (sin cambios)
        int totalPacientes = pacienteDAO.contarPacientes();
        int totalCitas = citaDAO.contarCitas();
        int citasHoy = citaDAO.contarCitasHoy();
        int citasProgramadas = citaDAO.contarCitasPorEstado("PROGRAMADA");
        int citasConfirmadas = citaDAO.contarCitasPorEstado("CONFIRMADA");
        int citasAtendidas = citaDAO.contarCitasPorEstado("ATENDIDA");
        int citasCanceladas = citaDAO.contarCitasPorEstado("CANCELADA");
        int totalMedicos = usuarioDAO.contarUsuariosPorRol("MEDICO");
        int totalEnfermeros = usuarioDAO.contarUsuariosPorRol("ENFERMERO");
        int totalRecepcionistas = usuarioDAO.contarUsuariosPorRol("RECEPCIONISTA");
        int totalHorarios = horarioDAO.contarHorarios();

        req.setAttribute("totalPacientes", totalPacientes);
        req.setAttribute("totalCitas", totalCitas);
        req.setAttribute("citasHoy", citasHoy);
        req.setAttribute("citasProgramadas", citasProgramadas);
        req.setAttribute("citasConfirmadas", citasConfirmadas);
        req.setAttribute("citasAtendidas", citasAtendidas);
        req.setAttribute("citasCanceladas", citasCanceladas);
        req.setAttribute("totalMedicos", totalMedicos);
        req.setAttribute("totalEnfermeros", totalEnfermeros);
        req.setAttribute("totalRecepcionistas", totalRecepcionistas);
        req.setAttribute("totalHorarios", totalHorarios);

        List<Cita> ultimasCitas = citaDAO.obtenerUltimasCitas(5);
        req.setAttribute("ultimasCitas", ultimasCitas);

        // Redirige según el rol
        String destino = "/views/dashboard.jsp";   // por defecto (recepcionista)
        if ("MEDICO".equals(rol)) {
            destino = "/views/medico/dashboard.jsp";
        } else if ("ENFERMERO".equals(rol)) {
            destino = "/views/enfermero/dashboard.jsp";
        }
        req.getRequestDispatcher(destino).forward(req, resp);
    }
}
