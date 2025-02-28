package backend.academy.scrapper.externalapi;

import backend.academy.scrapper.repository.Link;
import java.time.ZonedDateTime;

public interface ExternalApiRequest {
    ZonedDateTime checkLinkUpdate(Link link);
}
