package backend.academy.scrapper.externalapi.github;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.common.ErrorApplier;
import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.github.models.GithubResponse;
import backend.academy.scrapper.repository.entity.Link;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
