package dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogAcceso {

    private int id;
    private int idUsuario;
    private String username;
    private String accion;
    private String ip;
    private String resultado;  // "EXITO" o "FALLO"
    private LocalDateTime fecha;

    // Constructores
    public LogAcceso() {
    }

    public LogAcceso(int idUsuario, String username, String accion, String ip, String resultado) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.accion = accion;
        this.ip = ip;
        this.resultado = resultado;
    }

    // Getters y Setters (genera automáticamente en tu IDE)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getFechaFormateada() {
        if (fecha == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return fecha.format(formatter);
    }
}
