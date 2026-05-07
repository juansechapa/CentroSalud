package servlet;

import dao.UsuarioDAO;
import dao.UsuarioImpl;
import dto.Usuario;
import util.OTPService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/verificar-otp")
public class OtpVerificationServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String codigo = request.getParameter("codigo");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        Integer otpUserId = (Integer) session.getAttribute("otpUserId");
        if (otpUserId == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        // Validar OTP
        if (OTPService.esValido(otpUserId, codigo)) {
            // OTP válido: completar inicio de sesión
            Usuario usuario = usuarioDAO.obtenerPorId(otpUserId);
            if (usuario != null) {
                session.removeAttribute("otpUserId");
                session.setAttribute("usuario", usuario);
                session.setAttribute("rol", usuario.getRol());
                session.setAttribute("userId", usuario.getId());
                session.setMaxInactiveInterval(30 * 60);

                // Redirigir según rol
                switch (usuario.getRol()) {
                    case "MEDICO":
                        response.sendRedirect(request.getContextPath() + "/views/medico/dashboard.jsp");
                        break;
                    case "ENFERMERO":
                        response.sendRedirect(request.getContextPath() + "/views/enfermero/dashboard.jsp");
                        break;
                    default:
                        response.sendRedirect(request.getContextPath() + "/views/dashboard.jsp");
                        break;
                }
                return;
            }
        }

        // Código inválido
        request.setAttribute("error", "Código incorrecto o expirado. Intente de nuevo.");
        request.getRequestDispatcher("/views/otp_verificacion.jsp").forward(request, response);
    }
}
