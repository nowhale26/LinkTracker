package backend.academy.scrapper.externalapi.github;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.ExternalApiRequest;
import backend.academy.scrapper.externalapi.github.models.GithubResponse;
import backend.academy.scrapper.repository.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;

@Service
@Slf4j
public class GithubClient implements ExternalApiRequest {
    private final WebClient githubWebClient;

    @Value("${app.github-token:1234}")
    private String githubToken;

    public GithubClient(WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    @Override
    public ZonedDateTime checkLinkUpdate(Link link) {
        String githubLink = createGithubLink(link);
        githubWebClient
            .get()
            .uri(githubLink)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, GithubClient::applyError)
            .onStatus(HttpStatusCode::isError, GithubClient::applyError)
            .bodyToMono(GithubResponse.class)
            .block();
        return null;
    }


    private String createGithubLink(Link link){
        String url = link.getUrl();
        try{
            return new URI(url).getPath();
        } catch (URISyntaxException e) {
            throw new ScrapperException("Некорректный синтаксис ссылки","500","Ошибка URI");
        }
    }

    private static Mono<? extends Throwable> applyError(ClientResponse response) {
        logResponse(response);
        return response.bodyToMono(String.class).flatMap(error -> Mono.error(new ScrapperException(error,response.statusCode().toString())));
    }

    private static void logResponse(ClientResponse response) {
        if (log.isErrorEnabled()) {
            log.error("Response status: {}", response.statusCode());
            log.error("Response headers: {}", response.headers().asHttpHeaders());
            response.bodyToMono(String.class)
                .publishOn(Schedulers.boundedElastic())
                .subscribe(body -> log.error("Response body: {}", body));
        }
    }

}
