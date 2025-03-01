package backend.academy.scrapper.common.validator;

import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.repository.Link;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LinkValidator {

    private final Map<String, ExternalApiValidator> validators = new HashMap<>();

    public LinkValidator(List<ExternalApiValidator> validatorList) {
        for (var validtor : validatorList) {
            validators.put(validtor.getSiteName(), validtor);
        }
    }

    public void validateLink(Link link) {
        boolean validated = validators.get(link.getSiteName()).validateLink(link);
        if (!validated) {
            throw new BusinessException("Некорректная ссылка", "400",
                "Некорректные параметры запроса");
        }
    }
}
