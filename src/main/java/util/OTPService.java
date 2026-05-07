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

    public static void enviarOTP(String destinatario, String codigo) {
        if (!validarCredenciales()) {
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", EMAIL_HOST);
            props.put("mail.smtp.port", EMAIL_PORT);
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");
            props.put("mail.debug", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USER, EMAIL_PASS);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USER, "Sistema SaludBoyaca"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            message.setSubject("Código de verificación - SaludBoyaca");

            String html = "<div style='font-family:Arial;padding:20px;border:1px solid #ddd'>"
                    + "<h2 style='color:#1A5276'>Verificación SaludBoyaca</h2>"
                    + "<p>Tu código de acceso es:</p>"
                    + "<h1 style='letter-spacing:5px;background:#f4f4f4;padding:10px'>" + codigo + "</h1>"
                    + "<p>Válido por 5 minutos.</p>"
                    + "<hr><small>SaludBoyaca - Centro de Salud</small>"
                    + "</div>";
            message.setContent(html, "text/html; charset=utf-8");

            System.out.println("[OTP] Enviando a: " + destinatario);
            System.out.println("[OTP] Usando SMTP: " + EMAIL_HOST + ":" + EMAIL_PORT);

            Transport.send(message); // forma más simple y robusta

            System.out.println("[OTP] ✅ Correo enviado exitosamente");
        } catch (MessagingException e) {
            System.err.println("[OTP] ❌ Error SMTP:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[OTP] ❌ Error inesperado:");
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
