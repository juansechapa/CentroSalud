package servlet;

import dto.Paciente;
import dao.PacienteDAO;
import dao.PacienteImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/PacienteServlet")
public class PacienteServlet extends HttpServlet {

    private PacienteDAO pacienteDAO = new PacienteImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/views/login.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarPacientes(request, response);
                break;
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "editar":  // CORREGIDO: antes era "edita"
                mostrarFormularioEditar(request, response);
                break;
            case "eliminar":
                eliminarPaciente(request, response);
                break;
            default:
                listarPacientes(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "insertar";
        }

        switch (accion) {
            case "insertar":
                insertarPaciente(request, response);
                break;
            case "actualizar":
                actualizarPaciente(request, response);
                break;
            default:
                listarPacientes(request, response);
                break;
        }
    }

    private void listarPacientes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String buscar = request.getParameter("buscar");
        List<Paciente> pacientes;

        if (buscar != null && !buscar.trim().isEmpty()) {
            pacientes = pacienteDAO.buscarPorNombre(buscar);
        } else {
            pacientes = pacienteDAO.obtenerTodos();
        }
        request.setAttribute("listaPacientes", pacientes);
        request.setAttribute("buscar", buscar);
        // CORREGIDO: ruta correcta al JSP de listado
        request.getRequestDispatcher("/views/pacientes/lista.jsp").forward(request, response);
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String rol = (String) session.getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos para esta acción");
            return;
        }
        request.getRequestDispatcher("/views/pacientes/formulario.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
String rol = (String) session.getAttribute("rol");
if (!"RECEPCIONISTA".equals(rol)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos para esta acción");
    return;
}
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            Paciente paciente = pacienteDAO.obtenerPorId(id);
            if (paciente == null) {
                request.setAttribute("error", "Paciente no encontrado");
                listarPacientes(request, response);
                return;
            }
            request.setAttribute("paciente", paciente);
            request.getRequestDispatcher("/views/pacientes/formulario.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar");
        }
    }

    private void eliminarPaciente(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
String rol = (String) session.getAttribute("rol");
if (!"RECEPCIONISTA".equals(rol)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos para esta acción");
    return;
}
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            // CORREGIDO: llamada con un solo parámetro (sin idUsuario)
            boolean exito = pacienteDAO.eliminar(id);
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar&mensaje=Paciente eliminado correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar&error=No se pudo eliminar el paciente");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar");
        }
    }

    private void insertarPaciente(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
String rol = (String) session.getAttribute("rol");
if (!"RECEPCIONISTA".equals(rol)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos para esta acción");
    return;
}
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String documento = request.getParameter("documento");
        String fechaNacimientoStr = request.getParameter("fechaNacimiento");
        String telefono = request.getParameter("telefono");
        String email = request.getParameter("email");
        String eps = request.getParameter("eps");
        String veredaBarrio = request.getParameter("veredaBarrio");

        if (nombres == null || nombres.trim().isEmpty()
                || apellidos == null || apellidos.trim().isEmpty()
                || documento == null || documento.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=nuevo&error=Datos obligatorios faltantes");
            return;
        }

        Paciente paciente = new Paciente();
        paciente.setNombres(nombres.trim());
        paciente.setApellidos(apellidos.trim());
        paciente.setDocumento(documento.trim());
        if (fechaNacimientoStr != null && !fechaNacimientoStr.isEmpty()) {
            paciente.setFechaNacimiento(LocalDate.parse(fechaNacimientoStr));
        }
        paciente.setTelefono(telefono != null ? telefono.trim() : null);
        paciente.setEmail(email != null ? email.trim() : null);
        paciente.setEps(eps != null ? eps.trim() : null);
        paciente.setVeredaBarrio(veredaBarrio != null ? veredaBarrio.trim() : null);

        boolean exito = pacienteDAO.insertar(paciente);
        if (exito) {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar&mensaje=Paciente registrado correctamente");
        } else {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=nuevo&error=Error al registrar paciente");
        }
    }

    private void actualizarPaciente(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
String rol = (String) session.getAttribute("rol");
if (!"RECEPCIONISTA".equals(rol)) {
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos para esta acción");
    return;
}
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            String nombres = request.getParameter("nombres");
            String apellidos = request.getParameter("apellidos");
            String documento = request.getParameter("documento");
            String fechaNacimientoStr = request.getParameter("fechaNacimiento");
            String telefono = request.getParameter("telefono");
            String email = request.getParameter("email");
            String eps = request.getParameter("eps");
            String veredaBarrio = request.getParameter("veredaBarrio");

            Paciente paciente = new Paciente();
            paciente.setId(id);
            paciente.setNombres(nombres);
            paciente.setApellidos(apellidos);
            paciente.setDocumento(documento);
            if (fechaNacimientoStr != null && !fechaNacimientoStr.isEmpty()) {
                paciente.setFechaNacimiento(LocalDate.parse(fechaNacimientoStr));
            }
            paciente.setTelefono(telefono);
            paciente.setEmail(email);
            paciente.setEps(eps);
            paciente.setVeredaBarrio(veredaBarrio);

            boolean exito = pacienteDAO.actualizar(paciente);
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar&mensaje=Paciente actualizado correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=editar&id=" + id + "&error=Error al actualizar");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/PacienteServlet?accion=listar");
        }
    }
}
