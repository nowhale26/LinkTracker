package backend.academy.scrapper;

import backend.academy.scrapper.repository.JpaLinksRepository;
import backend.academy.scrapper.repository.JpaUserRepository;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.OrmLinkRepository;
import backend.academy.scrapper.repository.SqlLinkRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ScrapperApplicationConfig {
    private final String githubUrl;
    private final String stackoverflowUrl;
    private final String botUrl;
    private final String accessType;

    public ScrapperApplicationConfig(ScrapperConfig scrapperConfig) {
        this.githubUrl = scrapperConfig.githubUrl();
        this.stackoverflowUrl = scrapperConfig.stackoverflowUrl();
        this.botUrl = scrapperConfig.botUrl();
        this.accessType = scrapperConfig.accessType();
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

    @Bean
    @Primary
    public LinkRepository linkRepository(
            JdbcTemplate jdbcTemplate, JpaLinksRepository jpaLinkRepository, JpaUserRepository jpaUserRepository) {
        switch (accessType) {
            case "ORM":
                return new OrmLinkRepository(jpaLinkRepository, jpaUserRepository);
            case "SQL":
                return new SqlLinkRepository(jdbcTemplate);
            default:
                return new OrmLinkRepository(jpaLinkRepository, jpaUserRepository);
        }
    }
}
