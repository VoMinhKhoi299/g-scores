package vn.gscores.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * CORS: cho phap frontend (Render static site) goi API khi tram khac origin.
 *
 * Phan tich:
 * - Frontend deploy tren https://g-scores.onrender.com, backend tren
 *   https://g-scores-api.onrender.com -> 2 host khac nhau, trinh deu choi
 *   request cross-origin neu khong co du Access-Control-Allow-Origin.
 * - Phan tich moi goi cua frontend deu la GET + header Accept (safelisted),
 *   nen trinh deu khong giu preflight O: chi can tra ve header CORS thoi.
 *
 * Bien lai: CORS_ALLOWED_ORIGINS (tach dau phay). Mac dinh "*" vi API chi
 * doc du lieu coong, khong co secret. Ban mat an toan hon thi dat
 * CORS_ALLOWED_ORIGINS=https://<static-site>.onrender.com tai Render.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");
    }
}
