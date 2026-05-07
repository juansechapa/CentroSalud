package util;

import dao.LogAccesoDAO;
import dao.LogAccesoImpl;
import dto.LogAcceso;
import dto.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AuditService {

    private static LogAccesoDAO logDAO = new LogAccesoImpl();

    /**
     * Registra una acción en la bitácora de accesos.
     *
     * @param request HttpServletRequest (para obtener IP y sesión)
     * @param accion Descripción de la acción (ej. "INSERT_PACIENTE",
     * "LOGIN_EXITO")
     * @param resultado "EXITO" o "FALLO"
     */
    public static void registrar(HttpServletRequest request, String accion, String resultado) {
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;
        int idUsuario = (usuario != null) ? usuario.getId() : 0;
        String username = (usuario != null) ? usuario.getUsername() : "ANONIMO";
        String ip = obtenerIP(request);

        LogAcceso log = new LogAcceso(idUsuario, username, accion, ip, resultado);
        logDAO.insertar(log);
    }

    /**
     * Sobrecarga útil cuando ya tienes el usuario e IP directamente (por
     * ejemplo en servlets de login antes de crear sesión).
     */
    public static void registrar(int idUsuario, String username, String accion, String ip, String resultado) {
        LogAcceso log = new LogAcceso(idUsuario, username, accion, ip, resultado);
        logDAO.insertar(log);
    }

    private static String obtenerIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
