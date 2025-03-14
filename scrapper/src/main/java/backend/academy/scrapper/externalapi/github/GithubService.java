package backend.academy.scrapper.externalapi.github;

import backend.academy.scrapper.externalapi.ExternalApi;
import backend.academy.scrapper.repository.Link;

public class GithubService implements ExternalApi {

    private static final String siteName = "github";

    @Override
    public String checkLinkUpdate(Link link) {
        return "";
    }

    @Override
    public String getSiteName() {
        return "";
    }
}
