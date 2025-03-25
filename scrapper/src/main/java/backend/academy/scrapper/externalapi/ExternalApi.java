package backend.academy.scrapper.externalapi;

import backend.academy.scrapper.repository.entity.Link;

public interface ExternalApi {
    String checkLinkUpdate(Link link);

    String getSiteName();
}
