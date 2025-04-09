package backend.academy.scrapper.common.validator;

import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.repository.entity.Link;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LinkValidator {

    private final Map<String, ExternalApiValidator> validators = new HashMap<>();

    public LinkValidator(List<ExternalApiValidator> validatorList) {
        for (var validator : validatorList) {
            validators.put(validator.getSiteName(), validator);
        }
    }

    public void validateLink(Link link) {
        boolean validated = validators.get(link.getSiteName()).validateLink(link);
        if (!validated) {
            throw new BusinessException("Некорректная ссылка", "400", "Некорректные параметры запроса");
        }
    }
}
