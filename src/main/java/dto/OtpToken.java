/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.time.LocalDateTime;

/**
 *
 * @author Juan
 */
public class OtpToken {

    private int id;
    private int idUsuario;
    private String codigo;
    private LocalDateTime fechaGen;
    private LocalDateTime expiraEn;
    private boolean usado;

    // Constructor vacío
    public OtpToken() {
    }

    // Constructor completo
    public OtpToken(int id, int idUsuario, String codigo,
            LocalDateTime fechaGen, LocalDateTime expiraEn,
            boolean usado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.codigo = codigo;
        this.fechaGen = fechaGen;
        this.expiraEn = expiraEn;
        this.usado = usado;
    }

    // Getters y Setters
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFechaGen() {
        return fechaGen;
    }

    public void setFechaGen(LocalDateTime fechaGen) {
        this.fechaGen = fechaGen;
    }

    public LocalDateTime getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(LocalDateTime expiraEn) {
        this.expiraEn = expiraEn;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }
}
