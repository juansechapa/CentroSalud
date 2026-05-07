package dto;

public class Usuario {

    private int id;
    private String nombres;
    private String apellidos;
    private String documento;
    private String email;
    private String username;
    private String password;
    private String rol;
    private String especialidad;
    private String lang_preferido;
    private boolean activo;

    public Usuario() {
    }

    public Usuario(int id, String nombres, String apellidos, String documento, String email, String username, String password, String rol, String especialidad, String lang_preferido, boolean activo) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.documento = documento;
        this.email = email;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.especialidad = especialidad;
        this.lang_preferido = lang_preferido;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getLang_preferido() {
        return lang_preferido;
    }

    public void setLang_preferido(String lang_preferido) {
        this.lang_preferido = lang_preferido;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
