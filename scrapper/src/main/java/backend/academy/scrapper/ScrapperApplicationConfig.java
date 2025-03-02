package backend.academy.scrapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ScrapperApplicationConfig {
    private String githubUrl;
    private String stackoverflowUrl;
    private String botUrl;

    public ScrapperApplicationConfig(ScrapperConfig scrapperConfig) {
        this.githubUrl = scrapperConfig.githubUrl();
        this.stackoverflowUrl = scrapperConfig.stackoverflowUrl();
        this.botUrl = scrapperConfig.botUrl();
    }

    @Bean
    public WebClient githubWebClient() {
        return WebClient.builder().baseUrl(githubUrl).build();
    }

    @Bean
    public WebClient botWebClient() {
        return WebClient.builder().baseUrl(botUrl).build();
    }

    @Bean
    public WebClient stackoverflowWebClient() {
        return WebClient.builder().baseUrl(stackoverflowUrl).build();
    }
}
