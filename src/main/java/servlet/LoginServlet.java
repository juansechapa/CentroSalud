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
import util.AuditService;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            Usuario u = (Usuario) session.getAttribute("usuario");
            redirigirPorRol(response, u.getRol(), request.getContextPath());
            return;
        }
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String ip = request.getRemoteAddr();

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Usuario y contraseña son obligatorios");
            return;
        }

        Usuario usuario = usuarioDAO.obtenerPorUsername(username.trim());

        if (usuario != null && usuario.isActivo() && usuario.getPassword().equals(password)) {
            // Credenciales correctas
            AuditService.registrar(usuario.getId(), usuario.getUsername(), "LOGIN_EXITO", ip, "EXITO");

            String codigo = OTPService.generarOTP(usuario.getId());
            OTPService.enviarOTP(usuario.getEmail(), codigo);

            HttpSession session = request.getSession();
            session.setAttribute("otpUserId", usuario.getId());
            session.setMaxInactiveInterval(5 * 60);

            response.sendRedirect(request.getContextPath() + "/views/otp_verificacion.jsp");
        } else {
            // Credenciales incorrectas
            int idUsuario = (usuario != null) ? usuario.getId() : 0;
            AuditService.registrar(idUsuario, username, "LOGIN_FALLO", ip, "FALLO");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Credenciales incorrectas o usuario inactivo");
        }
    }

    private void redirigirPorRol(HttpServletResponse response, String rol, String contextPath) throws IOException {
        switch (rol) {
            case "MEDICO":
                response.sendRedirect(contextPath + "/views/medico/dashboard.jsp");
                break;
            case "ENFERMERO":
                response.sendRedirect(contextPath + "/views/enfermero/dashboard.jsp");
                break;
            default:
                response.sendRedirect(contextPath + "/views/dashboard.jsp");
        }
    }
}
