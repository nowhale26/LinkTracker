package backend.academy.scrapper.externalapi.github;

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
    private final WebClient githubWebClient;

    @Value("${app.github-token:1234}")
    private String githubToken;

    public GithubClient(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }


    public GithubResponse checkLinkUpdate(Link link) {
        String requestLink = createRequestLink(link);
        return githubWebClient
            .get()
            .uri(requestLink)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, GithubClient::applyError)
            .onStatus(HttpStatusCode::isError, GithubClient::applyError)
            .bodyToMono(GithubResponse.class)
            .block();
    }

    private String createRequestLink(Link link) {
        String url = link.getUrl();
        try {
            return new URI(url).getPath();
        } catch (URISyntaxException e) {
            throw new ScrapperException("Некорректный синтаксис ссылки", "500", "Ошибка URI");
        }
    }

    private static Mono<? extends Throwable> applyError(ClientResponse response) {
        logResponse(response);
        return response.bodyToMono(String.class)
            .flatMap(error -> Mono.error(
                new ScrapperException(error, response.statusCode().toString())));
    }

    private static void logResponse(ClientResponse response) {
        if (log.isErrorEnabled()) {
            log.info("Response status: {}", response.statusCode());
            log.info("Response headers: {}", response.headers().asHttpHeaders());
            response.bodyToMono(String.class)
                .publishOn(Schedulers.boundedElastic())
                .subscribe(body -> log.error("Response body: {}", body));
        }
    }
}
