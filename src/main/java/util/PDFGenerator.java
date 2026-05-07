package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dto.Paciente;
import dto.Cita;
import dto.Horario;
import dto.LogAcceso;
import dto.Usuario;
import jakarta.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFGenerator {

    private static final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public static void generarPacientesPDF(List<Paciente> pacientes, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=pacientes.pdf");
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Paragraph titulo = new Paragraph("Listado de Pacientes", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        String[] headers = {"ID", "Documento", "Nombres", "Apellidos", "Teléfono", "Email", "EPS", "Vereda/Barrio"};
        for (String h : headers) {
            table.addCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }
        for (Paciente p : pacientes) {
            table.addCell(String.valueOf(p.getId()));
            table.addCell(p.getDocumento());
            table.addCell(p.getNombres());
            table.addCell(p.getApellidos());
            table.addCell(p.getTelefono() != null ? p.getTelefono() : "");
            table.addCell(p.getEmail() != null ? p.getEmail() : "");
            table.addCell(p.getEps() != null ? p.getEps() : "");
            table.addCell(p.getVeredaBarrio() != null ? p.getVeredaBarrio() : "");
        }
        document.add(table);
        document.close();
    }

    public static void generarCitasPDF(List<Cita> citas, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=citas.pdf");
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Paragraph titulo = new Paragraph("Listado de Citas", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        String[] headers = {"ID", "Paciente", "Médico", "Especialidad", "Fecha", "Hora", "Motivo", "Estado"};
        for (String h : headers) {
            table.addCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }
        for (Cita c : citas) {
            table.addCell(String.valueOf(c.getId()));
            table.addCell(c.getNombrePaciente() != null ? c.getNombrePaciente() : "");
            table.addCell(c.getNombreMedico() != null ? c.getNombreMedico() : "");
            table.addCell(c.getNombreEspecialidad() != null ? c.getNombreEspecialidad() : "");
            table.addCell(c.getFechaCita() != null ? c.getFechaCita().format(dateFmt) : "");
            table.addCell(c.getHoraCita() != null ? c.getHoraCita().format(timeFmt) : "");
            table.addCell(c.getMotivo() != null ? c.getMotivo() : "");
            table.addCell(c.getEstado() != null ? c.getEstado() : "");
        }
        document.add(table);
        document.close();
    }

    public static void generarLogsPDF(List<LogAcceso> logs, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=auditoria.pdf");
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("Auditoría de Accesos", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        String[] headers = {"ID", "ID Usuario", "Username", "Acción", "IP", "Resultado", "Fecha/Hora"};
        for (String h : headers) {
            table.addCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (LogAcceso log : logs) {
            table.addCell(String.valueOf(log.getId()));
            table.addCell(String.valueOf(log.getIdUsuario()));
            table.addCell(log.getUsername());
            table.addCell(log.getAccion());
            table.addCell(log.getIp());
            table.addCell(log.getResultado());
            String fechaStr = log.getFecha() != null ? log.getFecha().format(formatter) : "";
            table.addCell(fechaStr);
        }
        document.add(table);
        document.close();
    }

    // Generar PDF de Horarios
    public static void generarHorariosPDF(List<Horario> horarios, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=horarios.pdf");
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Paragraph titulo = new Paragraph("Listado de Horarios", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        String[] headers = {"ID", "ID Médico", "Día", "Hora Inicio", "Hora Fin", "Máx. Citas"};
        for (String h : headers) {
            table.addCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }
        for (Horario h : horarios) {
            table.addCell(String.valueOf(h.getId()));
            table.addCell(String.valueOf(h.getIdMedico()));
            table.addCell(obtenerNombreDia(h.getDiaSemana()));
            table.addCell(h.getHoraInicio().toString());
            table.addCell(h.getHoraFin().toString());
            table.addCell(String.valueOf(h.getMaxCitas()));
        }
        document.add(table);
        document.close();
    }

// Generar PDF de Usuarios
    public static void generarUsuariosPDF(List<Usuario> usuarios, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.pdf");
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Paragraph titulo = new Paragraph("Listado de Empleados", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        String[] headers = {"ID", "Username", "Nombres", "Apellidos", "Documento", "Email", "Rol", "Especialidad", "Activo"};
        for (String h : headers) {
            table.addCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        }
        for (Usuario u : usuarios) {
            table.addCell(String.valueOf(u.getId()));
            table.addCell(u.getUsername());
            table.addCell(u.getNombres());
            table.addCell(u.getApellidos());
            table.addCell(u.getDocumento());
            table.addCell(u.getEmail());
            table.addCell(u.getRol());
            table.addCell(u.getEspecialidad() != null ? u.getEspecialidad() : "");
            table.addCell(u.isActivo() ? "Activo" : "Inactivo");
        }
        document.add(table);
        document.close();
    }

// Método auxiliar (puede estar en otro lugar o repetido)
    private static String obtenerNombreDia(int dia) {
        switch (dia) {
            case 1:
                return "Lunes";
            case 2:
                return "Martes";
            case 3:
                return "Miércoles";
            case 4:
                return "Jueves";
            case 5:
                return "Viernes";
            case 6:
                return "Sábado";
            case 7:
                return "Domingo";
            default:
                return "";
        }
    }

    public static void generarComprobanteCita(Cita cita, HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=cita_" + cita.getId() + ".pdf");
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Comprobante de Cita Médica", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Información de la cita (con formato)
        document.add(new Paragraph("ID de cita: " + cita.getId()));
        document.add(new Paragraph("Paciente: " + cita.getNombrePaciente()));
        document.add(new Paragraph("Documento: " + cita.getDocumentoPaciente()));
        document.add(new Paragraph("Médico: " + cita.getNombreMedico()));
        document.add(new Paragraph("Especialidad: " + cita.getNombreEspecialidad()));
        document.add(new Paragraph("Fecha: " + cita.getFechaCita().toString()));
        document.add(new Paragraph("Hora: " + cita.getHoraCita().toString()));
        document.add(new Paragraph("Motivo: " + (cita.getMotivo() != null ? cita.getMotivo() : "No especificado")));
        document.add(new Paragraph("Estado: " + cita.getEstado()));
        document.add(new Paragraph("Observaciones: " + (cita.getObservaciones() != null ? cita.getObservaciones() : "")));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Este comprobante es generado por el sistema de SaludBoyaca."));

        document.close();
    }
}
