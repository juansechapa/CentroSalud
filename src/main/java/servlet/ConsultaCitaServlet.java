package servlet;

import dto.Cita;
import dao.CitaDAO;
import dao.CitaImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/consulta-cita")
public class ConsultaCitaServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Mostrar el formulario de consulta
        request.getRequestDispatcher("/views/consulta_cita.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String documento = request.getParameter("documento");
        String captchaUser = request.getParameter("captcha");
        HttpSession session = request.getSession();
        String captchaReal = (String) session.getAttribute("captchaText");

        // Validar CAPTCHA
        if (captchaReal == null || !captchaReal.equalsIgnoreCase(captchaUser)) {
            request.setAttribute("error", "El código CAPTCHA ingresado es incorrecto.");
            request.getRequestDispatcher("/views/consulta_cita.jsp").forward(request, response);
            return;
        }

        // Limpiar CAPTCHA de sesión para evitar reutilización
        session.removeAttribute("captchaText");

        // Buscar citas por documento del paciente
        if (documento == null || documento.trim().isEmpty()) {
            request.setAttribute("error", "Debe ingresar un número de documento.");
            request.getRequestDispatcher("/views/consulta_cita.jsp").forward(request, response);
            return;
        }

        List<Cita> citas = citaDAO.obtenerPorDocumentoPaciente(documento.trim());
        request.setAttribute("citas", citas);
        request.setAttribute("documento", documento);
        request.getRequestDispatcher("/views/consulta_cita.jsp").forward(request, response);
    }
}