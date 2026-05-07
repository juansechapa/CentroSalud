package servlet;

import dto.Cita;
import dto.Usuario;
import dao.*;
import util.PDFGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@WebServlet("/CitaServlet")
public class CitaServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaImpl();
    private PacienteDAO pacienteDAO = new PacienteImpl();
    private UsuarioDAO usuarioDAO = new UsuarioImpl();
    private EspecialidadDAO especialidadDAO = new EspecialidadImpl();
    private HorarioDAO horarioDAO = new HorarioImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
                listarCitas(request, response);
                break;
            case "nuevo":
                mostrarFormularioNuevo(request, response);
                break;
            case "editar":
                mostrarFormularioEditar(request, response);
                break;
            case "eliminar":
                eliminarCita(request, response);
                break;
            case "cambiarEstado":
                cambiarEstadoCita(request, response);
                break;
            case "exportar":
                exportarPDF(request, response);
                break;
            default:
                listarCitas(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "insertar";
        }
        switch (accion) {
            case "insertar":
                insertarCita(request, response);
                break;
            case "actualizar":
                actualizarCita(request, response);
                break;
            default:
                listarCitas(request, response);
        }

    }

    private void listarCitas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String rol = (String) session.getAttribute("rol");
        Integer idUsuario = (Integer) session.getAttribute("userId");
        List<Cita> citas;

        if ("MEDICO".equals(rol)) {
            // Médico solo ve sus propias citas
            citas = citaDAO.obtenerPorMedico(idUsuario);
        } else {
            // Recepcionista o enfermero ven todas (con filtros)
            String buscarPaciente = request.getParameter("buscarPaciente");
            String idMedicoStr = request.getParameter("idMedico");
            String estado = request.getParameter("estado");
            String fechaInicioStr = request.getParameter("fechaInicio");
            String fechaFinStr = request.getParameter("fechaFin");
            Integer idMedico = null;
            if (idMedicoStr != null && !idMedicoStr.isEmpty()) {
                idMedico = Integer.parseInt(idMedicoStr);
            }
            LocalDate fechaInicio = (fechaInicioStr != null && !fechaInicioStr.isEmpty()) ? LocalDate.parse(fechaInicioStr) : null;
            LocalDate fechaFin = (fechaFinStr != null && !fechaFinStr.isEmpty()) ? LocalDate.parse(fechaFinStr) : null;
            citas = citaDAO.obtenerConFiltros(buscarPaciente, idMedico, estado, fechaInicio, fechaFin);
        }

        request.setAttribute("listaCitas", citas);
        request.setAttribute("esEnfermero", "ENFERMERO".equals(rol));

        if ("RECEPCIONISTA".equals(rol)) {
            // Cargar datos para filtros
            request.setAttribute("medicos", usuarioDAO.obtenerPorRol("MEDICO"));
            request.setAttribute("estados", new String[]{"PROGRAMADA", "CONFIRMADA", "ATENDIDA", "CANCELADA"});
        }

        // Redirigir a la vista adecuada según rol
        if ("MEDICO".equals(rol)) {
            request.getRequestDispatcher("/views/medico/mis_citas.jsp").forward(request, response);
        } else if ("ENFERMERO".equals(rol)) {
            request.getRequestDispatcher("/views/enfermero/citas.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/views/citas/lista.jsp").forward(request, response);
        }
    }

    private void mostrarFormularioNuevo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Solo recepcionista puede crear nuevas citas (AuthFilter ya bloquea, pero redirigimos)
        String rol = (String) request.getSession().getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
            return;
        }
        cargarDatosFormulario(request);
        request.getRequestDispatcher("/views/citas/formulario.jsp").forward(request, response);
    }

    private void mostrarFormularioEditar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rol = (String) request.getSession().getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
            return;
        }
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            Cita cita = citaDAO.obtenerPorId(id);
            if (cita == null) {
                request.setAttribute("error", "Cita no encontrada");
                listarCitas(request, response);
                return;
            }
            request.setAttribute("cita", cita);
            cargarDatosFormulario(request);
            request.getRequestDispatcher("/views/citas/formulario.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
        }
    }

    private void cargarDatosFormulario(HttpServletRequest request) {
        request.setAttribute("pacientes", pacienteDAO.obtenerTodos());
        request.setAttribute("medicos", usuarioDAO.obtenerPorRol("MEDICO"));
        request.setAttribute("especialidades", especialidadDAO.obtenerTodas());
    }

    private void eliminarCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String rol = (String) request.getSession().getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            HttpSession session = request.getSession(false);
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();
            boolean exito = citaDAO.eliminar(id, usuario.getId(), ip);
            if (exito) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar&mensaje=Cita eliminada correctamente");
            } else {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar&error=No se pudo eliminar");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar");
        }
    }

    private void insertarCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String rol = (String) request.getSession().getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            int idPaciente = Integer.parseInt(request.getParameter("idPaciente"));
            int idMedico = Integer.parseInt(request.getParameter("idMedico"));
            int idEspecialidad = Integer.parseInt(request.getParameter("idEspecialidad"));
            LocalDate fechaCita = LocalDate.parse(request.getParameter("fechaCita"));
            LocalTime horaCita = LocalTime.parse(request.getParameter("horaCita"));
            String motivo = request.getParameter("motivo");
            String estado = request.getParameter("estado");
            String observaciones = request.getParameter("observaciones");
            HttpSession session = request.getSession(false);
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();

            // 1. Validar horario laboral del médico (tabla horarios)
            if (!horarioDAO.isHorarioLaboral(idMedico, fechaCita, horaCita)) {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=nuevo&error=El médico no trabaja en esa fecha/hora.");
                return;
            }

            // 2. Validar que no exista otra cita en menos de 20 minutos
            if (citaDAO.existeCitaCercana(idMedico, fechaCita, horaCita, 20)) {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=nuevo&error=Ya existe una cita muy cercana a esa hora (intervalo mínimo 20 minutos).");
                return;
            }

            // 3. Validar cita exacta a la misma hora
            if (citaDAO.existeCitaEnHorario(idMedico, fechaCita, horaCita)) {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=nuevo&error=El médico ya tiene una cita en esa fecha y hora exacta.");
                return;
            }

            Cita c = new Cita();
            // ... setear los datos
            c.setIdPaciente(idPaciente);
            c.setIdMedico(idMedico);
            c.setIdEspecialidad(idEspecialidad);
            c.setFechaCita(fechaCita);
            c.setHoraCita(horaCita);
            c.setMotivo(motivo);
            c.setEstado(estado);
            c.setObservaciones(observaciones);
            c.setIdRegistradoPor(usuario.getId());

            boolean exito = citaDAO.insertar(c, usuario.getId(), ip);
            if (exito) {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=listar&mensaje=Cita registrada correctamente");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=nuevo&error=Error al guardar la cita.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath()
                    + "/CitaServlet?accion=nuevo&error=Datos inválidos.");
        }
    }

    private void actualizarCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String rol = (String) request.getSession().getAttribute("rol");
        if (!"RECEPCIONISTA".equals(rol)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int idPaciente = Integer.parseInt(request.getParameter("idPaciente"));
            int idMedico = Integer.parseInt(request.getParameter("idMedico"));
            int idEspecialidad = Integer.parseInt(request.getParameter("idEspecialidad"));
            LocalDate fechaCita = LocalDate.parse(request.getParameter("fechaCita"));
            LocalTime horaCita = LocalTime.parse(request.getParameter("horaCita"));
            String motivo = request.getParameter("motivo");
            String estado = request.getParameter("estado");
            String observaciones = request.getParameter("observaciones");
            HttpSession session = request.getSession(false);
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            String ip = request.getRemoteAddr();

            // 1. Validar horario laboral
            if (!horarioDAO.isHorarioLaboral(idMedico, fechaCita, horaCita)) {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=editar&id=" + id + "&error=El médico no trabaja en esa fecha/hora.");
                return;
            }

            // 2. Validar que no exista otra cita cercana (excluyendo la actual)
            if (citaDAO.existeCitaCercanaExcepto(idMedico, fechaCita, horaCita, 20, id)) {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=editar&id=" + id + "&error=Ya existe otra cita muy cercana a esa hora (intervalo mínimo 20 minutos).");
                return;
            }

            // 3. Validar cita exacta duplicada (excluyendo la actual)
            if (citaDAO.existeCitaEnHorarioExcepto(idMedico, fechaCita, horaCita, id)) {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=editar&id=" + id + "&error=El médico ya tiene otra cita en esa fecha y hora exacta.");
                return;
            }

            Cita c = new Cita();
            c.setId(id);
            c.setIdPaciente(idPaciente);
            c.setIdMedico(idMedico);
            c.setIdEspecialidad(idEspecialidad);
            c.setFechaCita(fechaCita);
            c.setHoraCita(horaCita);
            c.setMotivo(motivo);
            c.setEstado(estado);
            c.setObservaciones(observaciones);

            boolean exito = citaDAO.actualizar(c, usuario.getId(), ip);
            if (exito) {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=listar&mensaje=Cita actualizada correctamente");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/CitaServlet?accion=editar&id=" + id + "&error=Error al actualizar la cita.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath()
                    + "/CitaServlet?accion=listar&error=Datos inválidos.");
        }
    }

    private void cambiarEstadoCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        String rol = (String) session.getAttribute("rol");
        if (!"MEDICO".equals(rol)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String idStr = request.getParameter("id");
        String nuevoEstado = request.getParameter("estado");
        if (idStr == null || nuevoEstado == null) {
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar&error=Datos inválidos");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            Integer idMedico = (Integer) session.getAttribute("userId");
            Cita c = citaDAO.obtenerPorId(id);
            if (c == null || c.getIdMedico() != idMedico) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar&error=No autorizado");
                return;
            }
            String estadoActual = c.getEstado();
            boolean cambioValido = false;
            if ("PROGRAMADA".equals(estadoActual) && ("CONFIRMADA".equals(nuevoEstado) || "CANCELADA".equals(nuevoEstado))) {
                cambioValido = true;
            } else if ("CONFIRMADA".equals(estadoActual) && ("ATENDIDA".equals(nuevoEstado) || "CANCELADA".equals(nuevoEstado))) {
                cambioValido = true;
            }
            if (!cambioValido) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar&error=Cambio no válido");
                return;
            }
            c.setEstado(nuevoEstado);
            String ip = request.getRemoteAddr();
            boolean ok = citaDAO.actualizar(c, idMedico, ip);
            if (ok) {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar&mensaje=Estado actualizado a " + nuevoEstado);
            } else {
                response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar&error=Error al actualizar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/CitaServlet?accion=listar&error=Error");
        }
    }

    private void exportarPDF(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String rol = (String) session.getAttribute("rol");
        List<Cita> citas;
        if ("MEDICO".equals(rol)) {
            Integer idMedico = (Integer) session.getAttribute("userId");
            citas = citaDAO.obtenerPorMedico(idMedico);
        } else {
            citas = citaDAO.obtenerTodas();
        }
        try {
            PDFGenerator.generarCitasPDF(citas, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generando PDF");
        }
    }
}
