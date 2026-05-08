package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
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
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Colores institucionales (puedes ajustar los códigos hexadecimales)
        BaseColor colorAzul = new BaseColor(26, 82, 118);      // #1A5276
        BaseColor colorVerde = new BaseColor(57, 169, 0);      // #39A900
        BaseColor colorGrisClaro = new BaseColor(245, 245, 245);
        BaseColor colorGrisBorde = new BaseColor(200, 200, 200);

        // Fuentes
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, colorAzul);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, colorVerde);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);

        // --- Cabecera con borde y fondo opcional ---
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 3});
        // Celda izquierda: podría ir un logo, aquí pongo un ícono de texto
        PdfPCell logoCell = new PdfPCell(new Phrase("🏥", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, colorAzul)));
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(logoCell);
        // Celda derecha: título principal
        PdfPCell titleCell = new PdfPCell(new Phrase("SALUDBOYACA", titleFont));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(titleCell);
        document.add(headerTable);

        // Línea decorativa
        Paragraph separator = new Paragraph();
        separator.add(new Chunk(new LineSeparator(1, 100, colorVerde, Element.ALIGN_CENTER, -1)));
        document.add(separator);
        document.add(Chunk.NEWLINE);

        // Título del comprobante
        Paragraph comprobanteTitle = new Paragraph("COMPROBANTE DE CITA MÉDICA", subtitleFont);
        comprobanteTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(comprobanteTitle);
        document.add(Chunk.NEWLINE);

        // --- Tabla de datos de la cita (dos columnas) ---
        PdfPTable dataTable = new PdfPTable(2);
        dataTable.setWidthPercentage(90);
        dataTable.setHorizontalAlignment(Element.ALIGN_CENTER);
        dataTable.setWidths(new float[]{1.5f, 2.5f});
        dataTable.setSpacingBefore(10f);
        dataTable.setSpacingAfter(10f);
        // Estilo de las celdas
        addLabelValueCell(dataTable, "ID de cita:", String.valueOf(cita.getId()), labelFont, valueFont);
        addLabelValueCell(dataTable, "Paciente:", cita.getNombrePaciente(), labelFont, valueFont);
        addLabelValueCell(dataTable, "Documento:", cita.getDocumentoPaciente(), labelFont, valueFont);
        addLabelValueCell(dataTable, "Médico:", cita.getNombreMedico(), labelFont, valueFont);
        addLabelValueCell(dataTable, "Especialidad:", cita.getNombreEspecialidad(), labelFont, valueFont);
        addLabelValueCell(dataTable, "Fecha:", cita.getFechaCita().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), labelFont, valueFont);
        addLabelValueCell(dataTable, "Hora:", cita.getHoraCita().format(DateTimeFormatter.ofPattern("HH:mm")), labelFont, valueFont);
        addLabelValueCell(dataTable, "Motivo:", (cita.getMotivo() != null ? cita.getMotivo() : "No especificado"), labelFont, valueFont);
        addLabelValueCell(dataTable, "Estado:", cita.getEstado(), labelFont, valueFont);
        addLabelValueCell(dataTable, "Observaciones:", (cita.getObservaciones() != null ? cita.getObservaciones() : ""), labelFont, valueFont);

        document.add(dataTable);

        // --- Tabla de información adicional (por ejemplo, datos de contacto) ---
        PdfPTable contactInfo = new PdfPTable(1);
        contactInfo.setWidthPercentage(90);
        contactInfo.setHorizontalAlignment(Element.ALIGN_CENTER);
        contactInfo.setSpacingBefore(15f);
        PdfPCell contactCell = new PdfPCell(new Phrase("Para cualquier cambio o cancelación, comuníquese al 01-8000-123456 o escriba a citas@saludboyaca.com", footerFont));
        contactCell.setBorder(Rectangle.BOX);
        contactCell.setPadding(8f);
        contactCell.setBackgroundColor(colorGrisClaro);
        contactInfo.addCell(contactCell);
        document.add(contactInfo);

        // Línea final y pie de página
        document.add(Chunk.NEWLINE);
        Paragraph footer = new Paragraph("Documento generado electrónicamente por SaludBoyaca - Sistema de Gestión de Citas", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        document.add(Chunk.NEWLINE);
        Paragraph fechaActual = new Paragraph("Fecha de emisión: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), footerFont);
        fechaActual.setAlignment(Element.ALIGN_CENTER);
        document.add(fechaActual);

        document.close();
    }

// Método auxiliar para agregar filas a la tabla de datos
    private static void addLabelValueCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBackgroundColor(new BaseColor(240, 248, 255)); // azul muy claro
        labelCell.setPadding(5f);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setPadding(5f);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(valueCell);
    }
}
