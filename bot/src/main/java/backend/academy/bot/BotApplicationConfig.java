package backend.academy.bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BotApplicationConfig {
    private String scrapperUrl;

    public BotApplicationConfig(BotConfig botConfig) {
        scrapperUrl = botConfig.scrapperUrl();
    }

    @Bean
    public WebClient scrapperWebClient() {
        return WebClient.builder().baseUrl(scrapperUrl).build();
    }
}
