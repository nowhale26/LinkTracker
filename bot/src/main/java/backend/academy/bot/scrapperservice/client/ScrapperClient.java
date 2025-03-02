package backend.academy.bot.scrapperservice.client;

import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.scrapperservice.client.model.AddLinkRequest;
import backend.academy.bot.scrapperservice.client.model.ApiErrorResponse;
import backend.academy.bot.scrapperservice.client.model.LinkResponse;
import backend.academy.bot.scrapperservice.client.model.ListLinksResponse;
import backend.academy.bot.scrapperservice.client.model.RemoveLinkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Slf4j
public class ScrapperClient {
    private final WebClient scrapperWebClient;

    public ScrapperClient(WebClient scrapperWebClient) {
        this.scrapperWebClient = scrapperWebClient;
    }

    public ListLinksResponse getLinks(Long userId) {
        return scrapperWebClient
                .get()
                .uri("/links")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("Tg-Chat-Id", userId.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ScrapperClient::applyError)
                .onStatus(HttpStatusCode::isError, ScrapperClient::applyError) // throw a functional exception
                .bodyToMono(ListLinksResponse.class)
                .block();
    }

    public LinkResponse addLink(Long userId, AddLinkRequest link) {
        return scrapperWebClient
                .post()
                .uri("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", userId.toString())
                .body(BodyInserters.fromValue(link))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ScrapperClient::applyError)
                .onStatus(HttpStatusCode::isError, ScrapperClient::applyError)
                .bodyToMono(LinkResponse.class)
                .block();
    }

    public LinkResponse removeLink(Long userId, RemoveLinkRequest link) {
        return scrapperWebClient
                .method(HttpMethod.DELETE)
                .uri("/links")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Tg-Chat-Id", userId.toString())
                .body(BodyInserters.fromValue(link))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ScrapperClient::applyError)
                .onStatus(HttpStatusCode::isError, ScrapperClient::applyError)
                .bodyToMono(LinkResponse.class)
                .block();
    }

    public void registerChat(Long userId) {
        scrapperWebClient
                .post()
                .uri("/tg-chat/{id}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ScrapperClient::applyError)
                .onStatus(HttpStatusCode::isError, ScrapperClient::applyError)
                .toBodilessEntity()
                .block();
    }

    public void deleteChat(Long userId) {
        scrapperWebClient
                .delete()
                .uri("/tg-chat/{id}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ScrapperClient::applyError)
                .onStatus(HttpStatusCode::isError, ScrapperClient::applyError)
                .toBodilessEntity()
                .block();
    }

    private static Mono<? extends Throwable> applyError(ClientResponse response) {
        return response.bodyToMono(ApiErrorResponse.class).flatMap(apiErrorResponse -> {
            if (log.isErrorEnabled()) {
                log.error("Response status: {}", response.statusCode());
                log.error("Response headers: {}", response.headers().asHttpHeaders());
                response.bodyToMono(String.class)
                        .publishOn(Schedulers.boundedElastic())
                        .subscribe(body -> log.error("Response body: {}", body));
            }
            String message = apiErrorResponse.getExceptionMessage();
            String code = apiErrorResponse.getCode();
            String name = apiErrorResponse.getExceptionName();
            return Mono.error(new ScrapperClientException(message, code, name));
        });
    }
}
