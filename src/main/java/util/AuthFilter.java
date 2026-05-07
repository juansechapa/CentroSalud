package util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter({"/dashboard", "/dashboard/*", "/pacientes/*", "/citas/*", "/horarios/*", "/usuarios/*",
    "/PacienteServlet", "/CitaServlet", "/HorarioServlet", "/UsuarioServlet"})
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/views/login.jsp", "/views/otp_verificacion.jsp",
            "/login", "/verificar-otp", "/reenviar-otp",
            "/captcha", "/consulta-cita", "/views/consulta_cita.jsp"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("usuario") != null);
        boolean pendingOtp = (session != null && session.getAttribute("otpUserId") != null);

        if (!loggedIn && !pendingOtp) {
            res.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        if (pendingOtp && !loggedIn) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Debe completar la verificación en dos pasos antes de continuar.");
            return;
        }

        String rol = (String) session.getAttribute("rol");
        String method = req.getMethod();
        boolean permitido = false;

        if ("RECEPCIONISTA".equals(rol)) {
            permitido = true;
        } else if ("MEDICO".equals(rol)) {
            // Permite acceder al servlet /dashboard y a sus vistas internas
            if (path.contains("/dashboard")) {
                permitido = true;
            }
            if (path.contains("CitaServlet") || path.contains("/citas/") || path.contains("/medico/")) {
                permitido = true;
            }
            if (path.contains("CitaServlet") && "exportar".equals(req.getParameter("accion"))) {
                permitido = true;
            }
        } else if ("ENFERMERO".equals(rol)) {
            // Permite acceder al servlet /dashboard y a sus vistas internas
            if (path.contains("/dashboard")) {
                permitido = true;
            }
            boolean isCitaGet = path.contains("CitaServlet") && method.equalsIgnoreCase("GET");
            boolean isPacienteGet = path.contains("PacienteServlet") && method.equalsIgnoreCase("GET");
            boolean isEnfermeroView = path.contains("/enfermero/");
            if (isCitaGet || isPacienteGet || isEnfermeroView) {
                permitido = true;
            }
        }

        if (permitido) {
            chain.doFilter(request, response);
        } else {
            res.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para acceder a esta página.");
        }
    }

    private boolean isPublicPath(String path) {
        for (String p : PUBLIC_PATHS) {
            if (path.contains(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
    }
}
