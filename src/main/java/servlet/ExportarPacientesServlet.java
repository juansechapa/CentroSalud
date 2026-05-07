package servlet;

import dao.PacienteDAO;
import dao.PacienteImpl;
import dto.Paciente;
import util.ExcelGenerator;
import util.PDFGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/exportar-pacientes")
public class ExportarPacientesServlet extends HttpServlet {

    private PacienteDAO pacienteDAO = new PacienteImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tipo = req.getParameter("tipo");
        List<Paciente> pacientes = pacienteDAO.obtenerTodos();
        try {
            if ("excel".equalsIgnoreCase(tipo)) {
                ExcelGenerator.exportarPacientesExcel(pacientes, resp);
            } else {
                PDFGenerator.generarPacientesPDF(pacientes, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte");
        }
    }
}
