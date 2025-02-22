package backend.academy.bot.service;

import backend.academy.bot.service.model.AddLinkRequest;
import backend.academy.bot.service.model.ListLinksResponse;
import backend.academy.bot.service.model.RemoveLinkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class ScrapperService {

    private final WebClient scrapperWebClient;

    public ScrapperService(WebClient scrapperWebClient) {
        this.scrapperWebClient = scrapperWebClient;
    }

    public ListLinksResponse getLinks(Long userId) {
        return scrapperWebClient
            .get()
            .uri("/links")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header("Tg-Chat-Id", userId.toString())
            .retrieve()
            .onStatus(HttpStatusCode::isError, ScrapperService::applyError) // throw a functional exception
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    public void addLink(Long userId, AddLinkRequest link) {

    }

    public void removeLink(Long userId, RemoveLinkRequest link) {

    }

    private static Mono<? extends Throwable> applyError(ClientResponse response) {
        logResponse(response);
        return response.bodyToMono(String.class).flatMap(error -> Mono.error(new RuntimeException(error)));
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
