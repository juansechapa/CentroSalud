package dao;

import dto.OtpToken;

public interface OTPTokenDAO {

    boolean insertar(OtpToken token);

    OtpToken obtenerTokenNoUsado(int idUsuario, String codigo);

    boolean marcarComoUsado(int id);

    void limpiarTokensExpirados();
}
