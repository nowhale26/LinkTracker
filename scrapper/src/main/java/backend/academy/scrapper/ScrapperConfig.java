package backend.academy.scrapper;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperConfig(
        @NotEmpty String githubToken,
        StackOverflowCredentials stackOverflow,
        @NotEmpty String githubUrl,
        @NotEmpty String stackoverflowUrl,
        @NotEmpty String botUrl,
        @NotEmpty String accessType,
        @NotEmpty String updateTopic,
        @NotEmpty String DLQTopic,
        @NotEmpty String messageTransport,
        @NotEmpty String cacheTtl) {
    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}
}
