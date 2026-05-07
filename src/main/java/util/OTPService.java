package util;

import dao.OTPTokenDAO;
import dao.OTPTokenImpl;
import dto.OtpToken;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;

public class OTPService {

    private static final int EXPIRACION_MINUTOS = 5;
    private static OTPTokenDAO tokenDAO = new OTPTokenImpl();

    // Credenciales cargadas desde config.properties
    private static String EMAIL_USER = null;
    private static String EMAIL_PASS = null;

    static {
        // Prioridad 1: Variables de Entorno (Producción)
        EMAIL_USER = System.getenv("EMAIL_USER");
        EMAIL_PASS = System.getenv("EMAIL_PASS");

        // Prioridad 2: Si no hay variables de entorno, buscar en config.properties (Desarrollo)
        if (EMAIL_USER == null || EMAIL_PASS == null) {
            try (InputStream input = OTPService.class.getClassLoader().getResourceAsStream("resources/config.properties")) {
                if (input != null) {
                    Properties prop = new Properties();
                    prop.load(input);
                    EMAIL_USER = prop.getProperty("email.user");
                    EMAIL_PASS = prop.getProperty("email.password");
                }
            } catch (Exception e) {
                System.err.println("[OTP] Error cargando config: " + e.getMessage());
            }
        }
    }

    public static String generarOTP(int idUsuario) {
        String codigo = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime expira = ahora.plusMinutes(EXPIRACION_MINUTOS);

        OtpToken token = new OtpToken();
        token.setIdUsuario(idUsuario);
        token.setCodigo(codigo);
        token.setFechaGen(ahora);
        token.setExpiraEn(expira);
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

    /**
     * Envía el código OTP por correo electrónico (si las credenciales están
     * configuradas). Siempre imprime el código en consola para depuración.
     *
     * @param email Correo destino
     * @param codigo Código OTP
     */
    public static void enviarOTP(String email, String codigo) {

        // 2. Si las credenciales no están cargadas, no intentar enviar correo
        if (EMAIL_USER == null || EMAIL_PASS == null || EMAIL_USER.trim().isEmpty() || EMAIL_PASS.trim().isEmpty()) {
            System.out.println("[OTP] Credenciales de correo no configuradas. No se enviará email real.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USER, EMAIL_PASS);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USER, "Sistema SaludBoyaca"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Código de verificación - Salubryaca");
            // Cambia message.setText por esto:
            String htmlContent = "<div style='font-family: Arial; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #2c3e50;'>Verificación SaludBoyaca</h2>"
                    + "<p>Hola, hemos recibido una solicitud de acceso.</p>"
                    + "<div style='background: #f4f4f4; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px;'>"
                    + codigo + "</div>"
                    + "<p style='color: #7f8c8d; font-size: 12px;'>Este código expira en 5 minutos.</p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            
            System.out.println("[OTP] Correo real enviado exitosamente a: " + email);
        } catch (MessagingException e) {
            System.err.println("[OTP] Error enviando correo real: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[OTP] Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
