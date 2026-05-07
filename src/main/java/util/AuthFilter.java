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

@WebFilter({
    "/dashboard", "/dashboard/*",
    "/pacientes/*", "/citas/*", "/horarios/*", "/usuarios/*",
    "/PacienteServlet", "/CitaServlet", "/HorarioServlet", "/UsuarioServlet"
})
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/views/login.jsp",
            "/views/otp_verificacion.jsp",
            "/login",
            "/verificar-otp",
            "/reenviar-otp",
            "/captcha",
            "/consulta-cita",
            "/views/consulta_cita.jsp"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // ✔️ NORMALIZAR PATH (IMPORTANTE)
        String path = req.getRequestURI();
        String ctx = req.getContextPath();

        if (path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }

        // ✔️ RUTAS PUBLICAS
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("usuario") != null);
        boolean pendingOtp = (session != null && session.getAttribute("otpUserId") != null);

        // ✔️ NO AUTENTICADO
        if (!loggedIn && !pendingOtp) {
            res.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }

        // ✔️ FLUJO OTP
        if (pendingOtp && !loggedIn) {

            boolean isOtpPage = path.endsWith("/views/otp_verificacion.jsp");
            boolean isOtpServlet = path.equals("/verificar-otp");

            if (!isOtpPage && !isOtpServlet) {
                res.sendRedirect(req.getContextPath() + "/views/otp_verificacion.jsp");
                return;
            }
        }

        String rol = (session != null) ? (String) session.getAttribute("rol") : null;
        String method = req.getMethod();

        boolean permitido = false;

        if ("RECEPCIONISTA".equals(rol)) {
            permitido = true;

        } else if ("MEDICO".equals(rol)) {

            if (path.startsWith("/dashboard")) {
                permitido = true;
            }

            if (path.contains("CitaServlet")
                    || path.startsWith("/citas/")
                    || path.startsWith("/medico/")) {
                permitido = true;
            }

            if ("exportar".equals(req.getParameter("accion"))
                    && path.contains("CitaServlet")) {
                permitido = true;
            }

        } else if ("ENFERMERO".equals(rol)) {

            if (path.startsWith("/dashboard")) {
                permitido = true;
            }

            boolean isCitaGet = path.contains("CitaServlet")
                    && method.equalsIgnoreCase("GET");

            boolean isPacienteGet = path.contains("PacienteServlet")
                    && method.equalsIgnoreCase("GET");

            boolean isEnfermeroView = path.startsWith("/enfermero/");

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
            if (path.endsWith(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
