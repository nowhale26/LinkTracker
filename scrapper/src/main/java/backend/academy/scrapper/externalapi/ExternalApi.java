package backend.academy.scrapper.externalapi;

import backend.academy.scrapper.repository.Link;
import java.time.ZonedDateTime;

public interface ExternalApi {
    ZonedDateTime checkLinkUpdate(Link link);
    String getSiteName();
}
