package backend.academy.scrapper.externalapi.github.apirequest;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.ExternalApiRequest;
import backend.academy.scrapper.repository.entity.Link;
import java.net.URI;
import java.net.URISyntaxException;

public interface GithubApiRequest extends ExternalApiRequest {

    default String createRequestLink(Link link) {
        String url = link.getUrl();
        try {
            return new URI(url).getPath();
        } catch (URISyntaxException e) {
            throw new ScrapperException("Некорректный синтаксис ссылки", "500", "Ошибка URI");
        }
    }

    @Override
    default String getSiteName() {
        return "github";
    }
}
