package backend.academy.scrapper.externalapi.github.apirequest;

import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.externalapi.github.GithubClient;
import backend.academy.scrapper.externalapi.github.GithubService;
import backend.academy.scrapper.externalapi.github.models.GithubResponse;
import backend.academy.scrapper.repository.entity.Link;
import org.springframework.stereotype.Component;

@Component
public class IssueRequest implements GithubApiRequest {
    private final GithubService githubService;
    private final GithubClient githubClient;

    public IssueRequest(GithubService githubService, GithubClient githubClient) {
        this.githubService = githubService;
        this.githubClient = githubClient;
    }

    @Override
    public ExternalApiResponse checkUpdate(Link link) {
        String requestLink = createRequestLink(link) + "/issues";
        GithubResponse[] response = githubClient.checkLinkUpdate(requestLink);
        return githubService.apiResponse(response);
    }

    @Override
    public String formMessage(ExternalApiResponse response, Link link) {
        return """
            Ссылка: %s
            Issue: %s
            Пользователь: %s
            Превью описания: %s
            """
                .formatted(link.getUrl(), response.getMainInfo(), response.getUserName(), response.getPreview());
    }
}
