package servlet;

import dao.LogAccesoDAO;
import dao.LogAccesoImpl;
import dto.LogAcceso;
import util.ExcelGenerator;
import util.PDFGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/exportar-logs")
public class ExportarLogsServlet extends HttpServlet {

    private LogAccesoDAO logDAO = new LogAccesoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }
        // Solo permitir a recepcionista (opcional)
        String rol = (String) session.getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos");
            return;
        }

        String tipo = req.getParameter("tipo");
        List<LogAcceso> logs = logDAO.obtenerTodos(); // método que devuelve List<LogAcceso>

        try {
            if ("excel".equalsIgnoreCase(tipo)) {
                ExcelGenerator.exportarLogsExcel(logs, resp);
            } else {
                PDFGenerator.generarLogsPDF(logs, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando el reporte");
        }
    }
}
