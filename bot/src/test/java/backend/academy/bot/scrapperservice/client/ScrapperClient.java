package backend.academy.bot.scrapperservice.client;

import backend.academy.bot.common.exception.BotException;
import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.scrapperservice.client.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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

@Profile("test")
@Component
@Slf4j
public class ScrapperClient {
    private final WebClient scrapperWebClient;

    public ScrapperClient(WebClient scrapperWebClient) {
        this.scrapperWebClient = scrapperWebClient;
    }

    public ListLinksResponse getLinks(Long userId) {
      throw new BotException("Некорректная ссылка на получение","400","Bad request");
    }

    public LinkResponse addLink(Long userId, AddLinkRequest link) {
        throw new BotException("Некорректная ссылка на добавление","400","Bad request");
    }

    public LinkResponse removeLink(Long userId, RemoveLinkRequest link) {
        throw new BotException("Некорректная ссылка на удаление","400","Bad request");
    }


}
