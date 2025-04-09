package backend.academy.scrapper.externalapi.github;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.common.ErrorApplier;
import backend.academy.scrapper.externalapi.github.models.GithubResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class GithubClient {
    private final String githubToken;
    private final WebClient githubWebClient;

    public GithubClient(ScrapperConfig scrapperConfig, WebClient githubWebClient) {
        this.githubToken = scrapperConfig.githubToken();
        this.githubWebClient = githubWebClient;
    }

    public GithubResponse[] checkLinkUpdate(String requestLink) {
        return githubWebClient
                .get()
                .uri(requestLink)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ErrorApplier::applyError)
                .onStatus(HttpStatusCode::isError, ErrorApplier::applyError)
                .bodyToMono(GithubResponse[].class)
                .block();
    }
}
