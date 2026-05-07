package servlet;

import dto.Cita;
import dao.CitaDAO;
import dao.CitaImpl;
import util.PDFGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/exportar-cita-individual")
public class ExportarCitaIndividualServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de cita no proporcionado");
            return;
        }
        try {
            int idCita = Integer.parseInt(idParam);
            Cita cita = citaDAO.obtenerPorId(idCita);
            if (cita == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Cita no encontrada");
                return;
            }
            PDFGenerator.generarComprobanteCita(cita, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar PDF");
        }
    }
}
