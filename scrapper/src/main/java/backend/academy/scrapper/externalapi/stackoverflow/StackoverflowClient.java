package backend.academy.scrapper.externalapi.stackoverflow;

import backend.academy.scrapper.common.ErrorApplier;
import backend.academy.scrapper.externalapi.stackoverflow.models.StackoverflowResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class StackoverflowClient {
    private final WebClient stackoverflowWebClient;
    private static final Map<String, String> filters = new HashMap<>();

    public StackoverflowClient(WebClient stackoverflowWebClient) {
        this.stackoverflowWebClient = stackoverflowWebClient;
        filters.put("answers", "!*Mg4Pjg8kg(ZzeJH");
        filters.put("comments", "!nNPvSN_ZTx");
    }

    public StackoverflowResponse checkLinkUpdate(String questionId, String type) {
        return stackoverflowWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{questionId}/{type}")
                        .queryParam("order", "desc")
                        .queryParam("sort", "creation")
                        .queryParam("site", "stackoverflow")
                        .queryParam("filter", "{filter}")
                        .build(questionId, type, filters.get(type)))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ErrorApplier::applyError)
                .onStatus(HttpStatusCode::isError, ErrorApplier::applyError)
                .bodyToMono(StackoverflowResponse.class)
                .block();
    }
}
