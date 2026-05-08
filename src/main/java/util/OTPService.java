package util;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import dao.OTPTokenDAO;
import dao.OTPTokenImpl;
import dto.OtpToken;

import java.time.LocalDateTime;
import java.util.Random;

public class OTPService {

    private static final int EXPIRACION_MINUTOS = 5;
    private static OTPTokenDAO tokenDAO = new OTPTokenImpl();

    static {
        System.out.println("[OTP] Inicializado con Resend API (sin SMTP)");
    }

    public static String generarOTP(int idUsuario) {
        String codigo = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime ahora = LocalDateTime.now();

        OtpToken token = new OtpToken();
        token.setIdUsuario(idUsuario);
        token.setCodigo(codigo);
        token.setFechaGen(ahora);
        token.setExpiraEn(ahora.plusMinutes(EXPIRACION_MINUTOS));
        token.setUsado(false);

        tokenDAO.insertar(token);
        return codigo;
    }

    public static boolean esValido(int idUsuario, String codigo) {
        OtpToken token = tokenDAO.obtenerTokenNoUsado(idUsuario, codigo);
        if (token != null) {
            tokenDAO.marcarComoUsado(token.getId());
            return true;
        }
        return false;
    }

    public static void enviarOTP(String email, String codigo) {
        String apiKey = System.getenv("RESEND_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("[OTP] No se ha configurado RESEND_API_KEY en el entorno.");
            return;
        }

        Resend resend = new Resend(apiKey);

        SendEmailRequest request = SendEmailRequest.builder()
                .from("onboarding@resend.dev")   // remitente de prueba (funciona sin verificar dominio)
                .to(email)
                .subject("Código de verificación - SaludBoyaca")
                .html("<div style='font-family:Arial;padding:20px;border:1px solid #ddd'>"
                        + "<h2 style='color:#1A5276'>Verificación SaludBoyaca</h2>"
                        + "<p>Tu código de acceso es:</p>"
                        + "<h1 style='letter-spacing:5px;background:#f4f4f4;padding:10px'>" + codigo + "</h1>"
                        + "<p>Válido por 5 minutos.</p>"
                        + "<hr><small>SaludBoyaca - Centro de Salud</small>"
                        + "</div>")
                .build();

        try {
            SendEmailResponse response = resend.emails().send(request);
            System.out.println("[OTP] Correo enviado correctamente a: " + email);
            System.out.println("[OTP] ID del mensaje en Resend: " + response.getId());
        } catch (ResendException e) {
            System.err.println("[OTP] Error al enviar correo con Resend: " + e.getMessage());
            e.printStackTrace();
        }
    }
}