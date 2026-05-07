package dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Cita {

    private int id;
    private int idPaciente;
    private int idMedico;
    private int idEspecialidad;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private String motivo;
    private String estado;   // PROGRAMADA, CONFIRMADA, ATENDIDA, CANCELADA
    private String observaciones;
    private LocalDateTime fechaRegistro;
    private Integer idRegistradoPor;

    // Campos adicionales para mostrar (no se guardan en BD)
    private String nombrePaciente;
    private String nombreMedico;
    private String nombreEspecialidad;
    private String documentoPaciente;
// getter y setter
    // Constructores, getters y setters (incluir los nuevos campos)

    public Cita() {
    }

    public Cita(int id, int idPaciente, int idMedico, int idEspecialidad, LocalDate fechaCita, LocalTime horaCita,
            String motivo, String estado, String observaciones, LocalDateTime fechaRegistro, Integer idRegistradoPor) {
        this.id = id;
        this.idPaciente = idPaciente;
        this.idMedico = idMedico;
        this.idEspecialidad = idEspecialidad;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.motivo = motivo;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaRegistro = fechaRegistro;
        this.idRegistradoPor = idRegistradoPor;
    }

    // Getters y setters (todos los campos)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public LocalDate getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(LocalDate fechaCita) {
        this.fechaCita = fechaCita;
    }

    public LocalTime getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(LocalTime horaCita) {
        this.horaCita = horaCita;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdRegistradoPor() {
        return idRegistradoPor;
    }

    public void setIdRegistradoPor(Integer idRegistradoPor) {
        this.idRegistradoPor = idRegistradoPor;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public String getDocumentoPaciente() {
        return documentoPaciente;
    }

    public void setDocumentoPaciente(String documentoPaciente) {
        this.documentoPaciente = documentoPaciente;
    }
    
}
