package backend.academy.scrapper.common.validator;

import backend.academy.scrapper.repository.model.Link;
import org.springframework.stereotype.Component;

@Component
public class StackoverflowLinkValidator implements ExternalApiValidator {
    private static final String siteName = "stackoverflow";

    @Override
    public boolean validateLink(Link link) {
        String regex = "^https://stackoverflow\\.com/questions/\\d+/[a-zA-Z0-9-]+$";
        return link.getUrl().matches(regex);
    }

    @Override
    public String getSiteName() {
        return siteName;
    }
}
