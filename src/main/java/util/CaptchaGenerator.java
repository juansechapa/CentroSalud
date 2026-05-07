package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

public class CaptchaGenerator {

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ123456789";
    private static final int LONGITUD = 5;
    private static final int ANCHO = 150;
    private static final int ALTO = 50;
    private static final Random random = new SecureRandom();

    /**
     * Genera un texto CAPTCHA aleatorio.
     */
    public static String generarTexto() {
        StringBuilder sb = new StringBuilder(LONGITUD);
        for (int i = 0; i < LONGITUD; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }

    /**
     * Genera una imagen CAPTCHA con el texto dado (con ruido y distorsión
     * leve).
     */
    public static BufferedImage generarImagen(String texto) {
        BufferedImage image = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // Fondo gris claro
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, ANCHO, ALTO);

        // Borde negro
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, ANCHO - 1, ALTO - 1);

        // Ruido: lineas aleatorias
        g.setColor(Color.GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(ANCHO);
            int y1 = random.nextInt(ALTO);
            int x2 = random.nextInt(ANCHO);
            int y2 = random.nextInt(ALTO);
            g.drawLine(x1, y1, x2, y2);
        }

        // Texto del CAPTCHA con fuente aleatoria y rotación simulada
        Font font = new Font("Arial", Font.BOLD, 28);
        g.setFont(font);
        g.setColor(Color.BLUE);
        int x = 20;
        for (char c : texto.toCharArray()) {
            int y = 35 + random.nextInt(10);
            g.drawString(String.valueOf(c), x, y);
            x += 25;
        }

        // Puntos de ruido
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < 200; i++) {
            g.drawOval(random.nextInt(ANCHO), random.nextInt(ALTO), 1, 1);
        }

        g.dispose();
        return image;
    }
}
