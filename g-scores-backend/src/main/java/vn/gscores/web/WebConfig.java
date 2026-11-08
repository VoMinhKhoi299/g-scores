package vn.gscores.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS: cho phep frontend goi API khi tram khac origin.
 *
 * Phan tich:
 * - Frontend deploy tren https://g-scores.onrender.com, backend tren
 *   https://g-scores-api.onrender.com -> 2 host khac nhau, trinh duyet
 *   se chan request cross-origin neu backend khong tra ve header
 *   Access-Control-Allow-Origin.
 * - Moi goi cua frontend deu la GET nhan (khong co secret header) nen
 *   trinh duyet khong phat preflight — chi can phan CORS tra ve duong
 *   la du. Vi vay config WebMvcConfigurer vay la day du, khong can
 *   @CrossOrigin truoc moi controller.
 *
 * Bien: CORS_ALLOWED_ORIGINS (tach dau phay, duoc trim). Mac dinh "*"
 * vi API chi doc du lieu coong, khong co secret. Muon han che then
 * tai Render: CORS_ALLOWED_ORIGINS=https://<ten-static-site>.onrender.com
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(o -> !o.isEmpty())
                .toArray(String[]::new);
        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");
    }
}
