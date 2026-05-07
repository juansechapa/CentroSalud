package servlet;

import dao.HorarioDAO;
import dao.HorarioImpl;
import dto.Horario;
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

@WebServlet("/exportar-horarios")
public class ExportarHorariosServlet extends HttpServlet {

    private HorarioDAO horarioDAO = new HorarioImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }
        String rol = (String) session.getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos");
            return;
        }
        String tipo = req.getParameter("tipo");
        List<Horario> horarios = horarioDAO.obtenerTodos();
        try {
            if ("excel".equalsIgnoreCase(tipo)) {
                ExcelGenerator.exportarHorariosExcel(horarios, resp);
            } else {
                PDFGenerator.generarHorariosPDF(horarios, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando reporte");
        }
    }
}
