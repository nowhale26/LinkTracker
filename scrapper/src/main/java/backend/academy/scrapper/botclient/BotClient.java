package backend.academy.scrapper.botclient;

import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.links.model.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
public class BotClient {

    @Autowired
    private WebClient botWebClient;

    public void sendUpdate(LinkUpdate linkUpdate) {
        botWebClient
                .post()
                .uri("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(linkUpdate))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, BotClient::applyError)
                .onStatus(HttpStatusCode::isError, BotClient::applyError)
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
            return Mono.error(new ScrapperException(message, code, name));
        });
    }
}
