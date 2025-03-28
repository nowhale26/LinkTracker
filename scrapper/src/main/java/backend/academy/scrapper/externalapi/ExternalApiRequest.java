package backend.academy.scrapper.externalapi;

import backend.academy.scrapper.repository.entity.Link;

public interface ExternalApiRequest {
    ExternalApiResponse checkUpdate(Link link);

    String formMessage(ExternalApiResponse response, Link link);

    String getSiteName();
}
