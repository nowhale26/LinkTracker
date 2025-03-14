package backend.academy.scrapper.externalapi.stackoverflow;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.ExternalApi;
import backend.academy.scrapper.externalapi.stackoverflow.models.StackoverflowResponse;
import backend.academy.scrapper.repository.Link;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
public class StackoverflowClient{
    private final WebClient stackoverflowWebClient;
    private static final String filter = "!apyOSAmJazrj_y";

    public StackoverflowClient(WebClient stackoverflowWebClient) {
        this.stackoverflowWebClient = stackoverflowWebClient;
    }

    public StackoverflowResponse checkLinkUpdate(Link link) {
        String id = getQuestionId(link);
        return stackoverflowWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{questionId}/answers")
                        .queryParam("order", "desc")
                        .queryParam("sort", "creation")
                        .queryParam("site", "stackoverflow")
                        .queryParam("filter", "{filter}")
                        .build(id, filter))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, StackoverflowClient::applyError)
                .onStatus(HttpStatusCode::isError, StackoverflowClient::applyError)
                .bodyToMono(StackoverflowResponse.class)
                .block();
    }

    private String getQuestionId(Link link) {
        String url = link.getUrl();
        UriTemplate template = new UriTemplate("https://stackoverflow.com/questions/{id}/{title}");
        Map<String, String> variables = template.match(url);
        String id = variables.get("id");
        return id;
    }

    private static Mono<? extends Throwable> applyError(ClientResponse response) {
        logResponse(response);
        return response.bodyToMono(String.class)
                .flatMap(error -> Mono.error(
                        new ScrapperException(error, response.statusCode().toString())));
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
