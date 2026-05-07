package servlet;

import dao.CitaDAO;
import dao.CitaImpl;
import dto.Cita;
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

@WebServlet("/exportar-citas")
public class ExportarCitasServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String rol = (String) session.getAttribute("rol");
        Integer userId = (Integer) session.getAttribute("userId");
        List<Cita> citas;
        if ("MEDICO".equals(rol)) {
            citas = citaDAO.obtenerPorMedico(userId);
        } else {
            citas = citaDAO.obtenerTodas();
        }
        String tipo = req.getParameter("tipo");
        try {
            if ("excel".equalsIgnoreCase(tipo)) {
                ExcelGenerator.exportarCitasExcel(citas, resp);
            } else {
                PDFGenerator.generarCitasPDF(citas, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte");
        }
    }
}
