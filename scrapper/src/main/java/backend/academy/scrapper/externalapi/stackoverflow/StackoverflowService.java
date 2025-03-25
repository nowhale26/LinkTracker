package backend.academy.scrapper.externalapi.stackoverflow;

import backend.academy.scrapper.externalapi.ExternalApi;
import backend.academy.scrapper.repository.entity.Link;

public class StackoverflowService implements ExternalApi {
    private static final String siteName = "stackoverflow";

    @Override
    public String checkLinkUpdate(Link link) {
        return "";
    }

    @Override
    public String getSiteName() {
        return "";
    }
}
