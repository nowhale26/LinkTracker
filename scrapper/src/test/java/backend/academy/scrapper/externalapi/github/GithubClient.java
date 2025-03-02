package backend.academy.scrapper.externalapi.github;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.ExternalApi;
import backend.academy.scrapper.repository.Link;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
@Slf4j
public class GithubClient implements ExternalApi {
    private final String siteName = "github";

    @Override
    public ZonedDateTime checkLinkUpdate(Link link) {
        if ("https://github.com/nowhale26/abc".equals(link.getUrl())) {
            return ZonedDateTime.now().plusHours(5);
        } else if ("https://github.com/nowhale26/def".equals(link.getUrl())) {
            throw new ScrapperException(siteName, "400", "Некорректный запрос");
        } else {
            return ZonedDateTime.now().minusHours(5);
        }
    }

    @Override
    public String getSiteName() {
        return siteName;
    }
}
