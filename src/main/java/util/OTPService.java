package util;

import dao.OTPTokenDAO;
import dao.OTPTokenImpl;
import dto.OtpToken;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private static final String BREVO_API_KEY;

    static {
        // Leer variables de entorno (obligatorias)
        EMAIL_USER = System.getenv("EMAIL_USER");
        EMAIL_PASS = System.getenv("EMAIL_PASS");
        EMAIL_HOST = System.getenv("EMAIL_HOST");
        EMAIL_PORT = System.getenv("EMAIL_PORT") != null ? System.getenv("EMAIL_PORT") : "587";
        BREVO_API_KEY = System.getenv("BREVO_API_KEY");

        if (EMAIL_USER == null || EMAIL_USER.isBlank()
                || EMAIL_PASS == null || EMAIL_PASS.isBlank()
                || EMAIL_HOST == null || EMAIL_HOST.isBlank()
                || BREVO_API_KEY == null || BREVO_API_KEY.isBlank()) {

            throw new RuntimeException(
                    "ERROR: Faltan variables de entorno."
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

        try {

            URL url = new URL("https://api.brevo.com/v3/smtp/email");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("accept", "application/json");
            con.setRequestProperty("api-key", BREVO_API_KEY);
            con.setRequestProperty("content-type", "application/json");

            con.setDoOutput(true);

            String jsonInputString
                    = "{"
                    + "\"sender\":{"
                    + "\"name\":\"SaludBoyaca\","
                    + "\"email\":\"" + EMAIL_USER + "\""
                    + "},"
                    + "\"to\":[{"
                    + "\"email\":\"" + email + "\""
                    + "}],"
                    + "\"subject\":\"Código OTP - SaludBoyaca\","
                    + "\"htmlContent\":\""
                    + "<div style='font-family:Arial,sans-serif;padding:20px'>"
                    + "<h2 style='color:#2563eb'>Verificación de acceso</h2>"
                    + "<p>Tu código OTP es:</p>"
                    + "<div style='font-size:32px;font-weight:bold;"
                    + "letter-spacing:5px;color:#111'>"
                    + codigo
                    + "</div>"
                    + "<p>Este código expira en 5 minutos.</p>"
                    + "<hr>"
                    + "<small>Sistema SaludBoyaca</small>"
                    + "</div>"
                    + "\""
                    + "}";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();

            System.out.println("[OTP] Brevo response: " + responseCode);

            BufferedReader br;

            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
            } else {
                br = new BufferedReader(
                        new InputStreamReader(con.getErrorStream(), "utf-8"));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;

            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            System.out.println("[OTP] RESPONSE BODY: " + response);

        } catch (Exception e) {
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
