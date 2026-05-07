package servlet;

import dao.UsuarioDAO;
import dao.UsuarioImpl;
import dto.Usuario;
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

@WebServlet("/exportar-usuarios")
public class ExportarUsuariosServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
            return;
        }
        String rol = (String) session.getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos");
            return;
        }
        String tipo = req.getParameter("tipo");
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        try {
            if ("excel".equalsIgnoreCase(tipo)) {
                ExcelGenerator.exportarUsuariosExcel(usuarios, resp);
            } else {
                PDFGenerator.generarUsuariosPDF(usuarios, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando reporte");
        }
    }
}
