package util;

import dto.Paciente;
import dto.Cita;
import dto.Horario;
import dto.LogAcceso;
import dto.Usuario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelGenerator {

    private static final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public static void exportarPacientesExcel(List<Paciente> pacientes, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pacientes");
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Documento", "Nombres", "Apellidos", "Teléfono", "Email", "EPS", "Vereda/Barrio"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(getHeaderStyle(workbook));
        }
        int rowNum = 1;
        for (Paciente p : pacientes) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getDocumento());
            row.createCell(2).setCellValue(p.getNombres());
            row.createCell(3).setCellValue(p.getApellidos());
            row.createCell(4).setCellValue(p.getTelefono() != null ? p.getTelefono() : "");
            row.createCell(5).setCellValue(p.getEmail() != null ? p.getEmail() : "");
            row.createCell(6).setCellValue(p.getEps() != null ? p.getEps() : "");
            row.createCell(7).setCellValue(p.getVeredaBarrio() != null ? p.getVeredaBarrio() : "");
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pacientes.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
        }
        workbook.close();
    }

    public static void exportarCitasExcel(List<Cita> citas, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Citas");
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Paciente", "Médico", "Especialidad", "Fecha", "Hora", "Motivo", "Estado"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(getHeaderStyle(workbook));
        }
        int rowNum = 1;
        for (Cita c : citas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getId());
            row.createCell(1).setCellValue(c.getNombrePaciente());
            row.createCell(2).setCellValue(c.getNombreMedico());
            row.createCell(3).setCellValue(c.getNombreEspecialidad());
            row.createCell(4).setCellValue(c.getFechaCita() != null ? c.getFechaCita().format(dateFmt) : "");
            row.createCell(5).setCellValue(c.getHoraCita() != null ? c.getHoraCita().format(timeFmt) : "");
            row.createCell(6).setCellValue(c.getMotivo());
            row.createCell(7).setCellValue(c.getEstado());
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=citas.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
        }
        workbook.close();
    }

    public static void exportarLogsExcel(List<LogAcceso> logs, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Auditoria");
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "ID Usuario", "Username", "Acción", "IP", "Resultado", "Fecha/Hora"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(getHeaderStyle(workbook));
        }
        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (LogAcceso log : logs) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(log.getId());
            row.createCell(1).setCellValue(log.getIdUsuario());
            row.createCell(2).setCellValue(log.getUsername());
            row.createCell(3).setCellValue(log.getAccion());
            row.createCell(4).setCellValue(log.getIp());
            row.createCell(5).setCellValue(log.getResultado());
            String fechaStr = log.getFecha() != null ? log.getFecha().format(formatter) : "";
            row.createCell(6).setCellValue(fechaStr);
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=auditoria.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
        }
        workbook.close();
    }

    // Exportar Horarios a Excel
    public static void exportarHorariosExcel(List<Horario> horarios, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Horarios");
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "ID Médico", "Día", "Hora Inicio", "Hora Fin", "Máx. Citas"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(getHeaderStyle(workbook));
        }
        int rowNum = 1;
        for (Horario h : horarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(h.getId());
            row.createCell(1).setCellValue(h.getIdMedico());
            row.createCell(2).setCellValue(obtenerNombreDia(h.getDiaSemana()));
            row.createCell(3).setCellValue(h.getHoraInicio().toString());
            row.createCell(4).setCellValue(h.getHoraFin().toString());
            row.createCell(5).setCellValue(h.getMaxCitas());
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=horarios.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
        }
        workbook.close();
    }

// Exportar Usuarios a Excel
    public static void exportarUsuariosExcel(List<Usuario> usuarios, HttpServletResponse response) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Usuarios");
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Username", "Nombres", "Apellidos", "Documento", "Email", "Rol", "Especialidad", "Activo"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(getHeaderStyle(workbook));
        }
        int rowNum = 1;
        for (Usuario u : usuarios) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(u.getId());
            row.createCell(1).setCellValue(u.getUsername());
            row.createCell(2).setCellValue(u.getNombres());
            row.createCell(3).setCellValue(u.getApellidos());
            row.createCell(4).setCellValue(u.getDocumento());
            row.createCell(5).setCellValue(u.getEmail());
            row.createCell(6).setCellValue(u.getRol());
            row.createCell(7).setCellValue(u.getEspecialidad() != null ? u.getEspecialidad() : "");
            row.createCell(8).setCellValue(u.isActivo() ? "Activo" : "Inactivo");
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=usuarios.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

// Método auxiliar para nombres de días
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

    private static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

}
