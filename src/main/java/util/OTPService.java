package util;

import dao.OTPTokenDAO;
import dao.OTPTokenImpl;
import dto.OtpToken;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;

public class OTPService {

    private static final int EXPIRACION_MINUTOS = 5;
    private static OTPTokenDAO tokenDAO = new OTPTokenImpl();

    private static final String EMAIL_USER;
    private static final String EMAIL_PASS;
    private static final String EMAIL_HOST;
    private static final String EMAIL_PORT;

    static {
        // Leer variables de entorno (obligatorias)
        EMAIL_USER = System.getenv("EMAIL_USER");
        EMAIL_PASS = System.getenv("EMAIL_PASS");
        EMAIL_HOST = System.getenv("EMAIL_HOST");
        EMAIL_PORT = System.getenv("EMAIL_PORT") != null ? System.getenv("EMAIL_PORT") : "587";

        if (EMAIL_USER == null || EMAIL_USER.isBlank()
                || EMAIL_PASS == null || EMAIL_PASS.isBlank()
                || EMAIL_HOST == null || EMAIL_HOST.isBlank()) {
            throw new RuntimeException(
                    "ERROR: Faltan variables de entorno para el envío de correos.\n"
                    + "Debes definir: EMAIL_USER, EMAIL_PASS, EMAIL_HOST"
            );
        }

        System.out.println("[OTP INIT] ✅ Credenciales cargadas");
        System.out.println("[OTP INIT] EMAIL_USER = " + EMAIL_USER);
        System.out.println("[OTP INIT] EMAIL_HOST = " + EMAIL_HOST);
        System.out.println("[OTP INIT] EMAIL_PORT = " + EMAIL_PORT);
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

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("[OTP] RESEND_API_KEY no configurada");
            return;
        }

        
        
        
        try {
            String json = "{"
                    + "\"from\":\"Sistema SaludBoyaca <onboarding@resend.dev>\","
                    + "\"to\":\"" + email + "\","
                    + "\"subject\":\"Código de verificación\","
                    + "\"html\":\"<div style='font-family:Arial'>"
                    + "<h2>Verificación SaludBoyaca</h2>"
                    + "<p>Tu código es:</p>"
                    + "<h1 style='letter-spacing:5px'>" + codigo + "</h1>"
                    + "<p>Expira en 5 minutos</p>"
                    + "</div>\""
                    + "}";

            java.net.URL url = new java.net.URL("https://api.resend.com/emails");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            System.out.println("[OTP] Resend response: " + responseCode);

        } catch (Exception e) {
            System.err.println("[OTP] Error enviando email con Resend:");
            e.printStackTrace();
        }
    }

    
    
    private static boolean validarCredenciales() {
        if (EMAIL_USER == null || EMAIL_PASS == null || EMAIL_HOST == null) {
            System.err.println("[OTP] Credenciales no disponibles. No se enviará correo.");
            return false;
        }
        return true;
    }
}
