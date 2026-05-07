package util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

@WebFilter("/*")
public class LocaleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();

        // Leer parámetro 'lang' de la URL
        String langParam = req.getParameter("lang");
        if (langParam != null && (langParam.equals("es") || langParam.equals("en") || langParam.equals("it"))) {
            session.setAttribute("lang", langParam);
        }

        // Obtener idioma guardado en sesión o por defecto 'es'
        String lang = (String) session.getAttribute("lang");
        if (lang == null) {
            lang = "es";
        }

        // Configurar el locale para JSTL
        Locale locale = new Locale(lang);
        // Forma estándar para que fmt:setLocale lo detecte automáticamente
        request.setAttribute("javax.servlet.jsp.jstl.fmt.locale.request", locale);
        // También se puede usar la sesión
        session.setAttribute("javax.servlet.jsp.jstl.fmt.locale.session", locale);

        // Configurar el locale de la respuesta HTTP
        res.setLocale(locale);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
