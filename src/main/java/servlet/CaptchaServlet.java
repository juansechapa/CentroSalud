package servlet;

import util.CaptchaGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@WebServlet("/captcha")
public class CaptchaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/jpeg");
        HttpSession session = request.getSession();
        String captchaText = CaptchaGenerator.generarTexto();
        session.setAttribute("captchaText", captchaText);
        BufferedImage captchaImage = CaptchaGenerator.generarImagen(captchaText);
        ImageIO.write(captchaImage, "jpg", response.getOutputStream());
    }
}