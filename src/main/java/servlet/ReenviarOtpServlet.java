package servlet;

import dao.OTPTokenDAO;
import dao.OTPTokenImpl;
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

@WebServlet("/reenviar-otp")
public class ReenviarOtpServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioImpl();
    private OTPTokenDAO tokenDAO = new OTPTokenImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Integer idUsuario = (Integer) session.getAttribute("otpUserId");
        if (idUsuario == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // Limpiar tokens anteriores no usados? No es obligatorio, pero para no acumular, puedes marcarlos como usados o simplemente generar uno nuevo.
        // Generar nuevo OTP
        String nuevoCodigo = OTPService.generarOTP(idUsuario);
        // Obtener email del usuario (podríamos guardarlo en sesión también)
        Usuario usuario = usuarioDAO.obtenerPorId(idUsuario);
        if (usuario != null) {
            OTPService.enviarOTP(usuario.getEmail(), nuevoCodigo);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
