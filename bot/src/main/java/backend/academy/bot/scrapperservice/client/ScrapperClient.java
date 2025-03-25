package backend.academy.bot.scrapperservice.client;

import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.scrapperservice.client.model.AddLinkRequest;
import backend.academy.bot.scrapperservice.client.model.ApiErrorResponse;
import backend.academy.bot.scrapperservice.client.model.LinkResponse;
import backend.academy.bot.scrapperservice.client.model.ListLinksResponse;
import backend.academy.bot.scrapperservice.client.model.RemoveLinkRequest;
import lombok.extern.slf4j.Slf4j;
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

    private <T, R> R executeRequest(HttpMethod method, String uri, Long userId, T requestBody, Class<R> responseType) {
        WebClient.RequestBodySpec requestSpec = scrapperWebClient
                .method(method)
                .uri(uri)
                .header("Tg-Chat-Id", userId != null ? userId.toString() : null);

        if (requestBody != null) {
            requestSpec.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(requestBody));
        }

        WebClient.ResponseSpec responseSpec = requestSpec
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ScrapperClient::applyError)
                .onStatus(HttpStatusCode::isError, ScrapperClient::applyError);

        if (responseType != null) {
            return responseSpec.bodyToMono(responseType).block();
        } else {
            return (R) responseSpec.toBodilessEntity().block();
        }
    }

    public ListLinksResponse getLinks(Long userId) {
        return executeRequest(HttpMethod.GET, "/links", userId, null, ListLinksResponse.class);
    }

    public LinkResponse addLink(Long userId, AddLinkRequest link) {
        return executeRequest(HttpMethod.POST, "/links", userId, link, LinkResponse.class);
    }

    public LinkResponse removeLink(Long userId, RemoveLinkRequest link) {
        return executeRequest(HttpMethod.DELETE, "/links", userId, link, LinkResponse.class);
    }

    public void registerChat(Long userId) {
        executeRequest(HttpMethod.POST, "/tg-chat/" + userId, null, null, null);
    }

    public void deleteChat(Long userId) {
        executeRequest(HttpMethod.DELETE, "/tg-chat/" + userId, null, null, null);
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
