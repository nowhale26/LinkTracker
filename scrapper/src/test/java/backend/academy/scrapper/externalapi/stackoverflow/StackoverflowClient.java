package backend.academy.scrapper.externalapi.stackoverflow;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.repository.entity.Link;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
@Slf4j
public class StackoverflowClient{
    private final String siteName = "stackoverflow";


    public ZonedDateTime checkLinkUpdate(Link link) {
        if ("https://stackoverflow.com/questions/1/a".equals(link.getUrl())) {
            return ZonedDateTime.now().plusHours(5);
        } else if ("https://stackoverflow.com/questions/2/b".equals(link.getUrl())) {
            throw new ScrapperException(siteName, "400", "Некорректный запрос");
        } else {
            return ZonedDateTime.now().minusHours(5);
        }
    }


    public String getSiteName() {
        return siteName;
    }
}
