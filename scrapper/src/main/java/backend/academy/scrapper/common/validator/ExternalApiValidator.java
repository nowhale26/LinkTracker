package backend.academy.scrapper.common.validator;

import backend.academy.scrapper.repository.Link;
import org.springframework.stereotype.Component;

@Component
public interface ExternalApiValidator {
    boolean validateLink(Link link);
    String getSiteName();
}
