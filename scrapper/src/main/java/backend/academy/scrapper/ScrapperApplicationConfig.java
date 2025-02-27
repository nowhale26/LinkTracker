package backend.academy.scrapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ScrapperApplicationConfig {
    @Value("${app.github-url:https://api.github.com/repos}")
    private String githubUrl;

    @Value("${app.bot-url:http://localhost:8080}")
    private String botUrl;

    @Bean
    public WebClient githubWebClient() {
        return WebClient.builder()
            .baseUrl(githubUrl)
            .build();
    }

    @Bean
    public WebClient botWebClient() {
        return WebClient.builder()
            .baseUrl(botUrl)
            .build();
    }
}
