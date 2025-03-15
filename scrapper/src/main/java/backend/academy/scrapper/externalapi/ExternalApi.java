package backend.academy.scrapper.externalapi;

import backend.academy.scrapper.repository.model.Link;

public interface ExternalApi {
    String checkLinkUpdate(Link link);

    String getSiteName();
}
