package backend.academy.scrapper.externalapi.github.apirequest;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.common.ErrorApplier;
import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.github.models.GithubResponse;
import backend.academy.scrapper.repository.entity.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import java.net.URI;
import java.net.URISyntaxException;

public class PRRequest implements GithubApiRequest {

    private final String githubToken;
    private final WebClient githubWebClient;

    public PRRequest(ScrapperConfig scrapperConfig, WebClient githubWebClient) {
        this.githubToken = scrapperConfig.githubToken();
        this.githubWebClient = githubWebClient;
    }

    @Override
    public GithubResponse checkUpdate(Link link) {
        String requestLink = createRequestLink(link);
        return githubWebClient
            .get()
            .uri(requestLink)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, ErrorApplier::applyError)
            .onStatus(HttpStatusCode::isError, ErrorApplier::applyError)
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
}
