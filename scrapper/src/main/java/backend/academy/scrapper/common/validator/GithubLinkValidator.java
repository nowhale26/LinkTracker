package backend.academy.scrapper.common.validator;

import backend.academy.scrapper.repository.entity.Link;
import org.springframework.stereotype.Component;

@Component
public class GithubLinkValidator implements ExternalApiValidator {

    private static final String siteName = "github";

    @Override
    public boolean validateLink(Link link) {
        String regex = "^https://github\\.com/[^/]+/[^/]+$";
        return link.getUrl().matches(regex);
    }

    @Override
    public String getSiteName() {
        return siteName;
    }
}
