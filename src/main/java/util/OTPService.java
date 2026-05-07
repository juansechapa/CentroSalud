package util;

import dao.OTPTokenDAO;
import dao.OTPTokenImpl;
import dto.OtpToken;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;

public class OTPService {

    private static final int EXPIRACION_MINUTOS = 5;
    private static OTPTokenDAO tokenDAO = new OTPTokenImpl();

    private static String EMAIL_USER;
    private static String EMAIL_PASS;

    // 🔹 CARGA DE CREDENCIALES
    static {
        EMAIL_USER = System.getenv("EMAIL_USER");
        EMAIL_PASS = System.getenv("EMAIL_PASS");

        if (EMAIL_USER == null || EMAIL_PASS == null) {
            throw new RuntimeException("EMAIL ENV NO CONFIGURADO EN RENDER");
        }

        System.out.println("[OTP INIT] EMAIL_USER = " + EMAIL_USER);
        System.out.println("[OTP INIT] EMAIL_PASS = OK");
    }

    // 🔹 GENERAR OTP
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

    // 🔹 VALIDAR OTP
    public static boolean esValido(int idUsuario, String codigo) {
        OtpToken token = tokenDAO.obtenerTokenNoUsado(idUsuario, codigo);

        if (token != null) {
            tokenDAO.marcarComoUsado(token.getId());
            return true;
        }
        return false;
    }

    // 🔹 ENVIAR OTP
    public static void enviarOTP(String email, String codigo) throws UnsupportedEncodingException {

        if (EMAIL_USER == null || EMAIL_PASS == null
                || EMAIL_USER.isEmpty() || EMAIL_PASS.isEmpty()) {

            System.out.println("[OTP] No hay credenciales, no se envía correo.");
            return;
        }

        try {
            Properties props = new Properties();

            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            // 🔴 DEBUG IMPORTANTE
            props.put("mail.debug", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_USER, EMAIL_PASS);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USER, "Sistema SaludBoyaca"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Código de verificación - SaludBoyaca");

            String html = "<div style='font-family:Arial;padding:20px'>"
                    + "<h2>Verificación SaludBoyaca</h2>"
                    + "<p>Tu código es:</p>"
                    + "<h1 style='letter-spacing:5px'>" + codigo + "</h1>"
                    + "<p>Expira en 5 minutos</p>"
                    + "</div>";

            message.setContent(html, "text/html; charset=utf-8");

            System.out.println("[OTP] Enviando correo a: " + email);

            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", EMAIL_USER, EMAIL_PASS);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            System.out.println("[OTP] Correo enviado correctamente.");

        } catch (MessagingException e) {
            System.err.println("[OTP] Error SMTP:");
            e.printStackTrace();
        }
    }
}
