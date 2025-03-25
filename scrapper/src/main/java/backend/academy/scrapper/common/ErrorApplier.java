package backend.academy.scrapper.common;

import backend.academy.scrapper.common.exception.ScrapperException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ErrorApplier {

    private ErrorApplier(){};

    public static Mono<? extends Throwable> applyError(ClientResponse response) {
        logResponse(response);
        return response.bodyToMono(String.class)
            .flatMap(error -> Mono.error(
                new ScrapperException(error, response.statusCode().toString())));
    }

    public static void logResponse(ClientResponse response) {
        if (log.isErrorEnabled()) {
            log.info("Response status: {}", response.statusCode());
            log.info("Response headers: {}", response.headers().asHttpHeaders());
            response.bodyToMono(String.class)
                .publishOn(Schedulers.boundedElastic())
                .subscribe(body -> log.error("Response body: {}", body));
        }
    }
}
