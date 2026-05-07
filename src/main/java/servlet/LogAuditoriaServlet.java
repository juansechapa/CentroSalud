package servlet;

import dao.LogAccesoDAO;
import dao.LogAccesoImpl;
import dto.LogAcceso;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/log-auditoria")
public class LogAuditoriaServlet extends HttpServlet {
    private LogAccesoDAO logDAO = new LogAccesoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }
        String rol = (String) session.getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos");
            return;
        }
        List<LogAcceso> logs = logDAO.obtenerTodos();
        request.setAttribute("logs", logs);
        request.getRequestDispatcher("/views/auditoria/logs.jsp").forward(request, response);
    }
}