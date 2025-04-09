package backend.academy.scrapper.externalapi.github;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.github.models.GithubResponse;
import backend.academy.scrapper.externalapi.github.models.GithubUser;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
@Slf4j
public class GithubClient {
    private final String siteName = "github";

    public GithubResponse[] checkLinkUpdate(String requestLink) {
        if ("/nowhale26/abc/issues".equals(requestLink)) {
            GithubResponse response = new GithubResponse();
            response.setCreatedAt(ZonedDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
            response.setComment("test");
            response.setDescription("test");
            response.setTitle("test");
            GithubUser user = new GithubUser();
            user.setLogin("test");
            response.setUser(user);
            GithubResponse[] responses = new GithubResponse[1];
            responses[0] = response;
            return responses;
        } else if ("https://github.com/nowhale26/def".equals(requestLink)) {
            throw new ScrapperException(siteName, "400", "Некорректный запрос");
        } else {
            GithubResponse response = new GithubResponse();
            response.setCreatedAt(ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
            response.setComment("test");
            response.setDescription("test");
            response.setTitle("test");
            GithubUser user = new GithubUser();
            user.setLogin("test");
            response.setUser(user);
            GithubResponse[] responses = new GithubResponse[1];
            responses[0] = response;
            return responses;
        }
    }

    public String getSiteName() {
        return siteName;
    }
}
