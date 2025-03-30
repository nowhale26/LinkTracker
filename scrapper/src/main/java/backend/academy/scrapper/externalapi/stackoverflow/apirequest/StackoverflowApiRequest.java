package backend.academy.scrapper.externalapi.stackoverflow.apirequest;

import backend.academy.scrapper.externalapi.ExternalApiRequest;
import backend.academy.scrapper.repository.entity.Link;
import java.util.Map;
import org.springframework.web.util.UriTemplate;

public interface StackoverflowApiRequest extends ExternalApiRequest {

    @Override
    default String getSiteName() {
        return "stackoverflow";
    }

    default String[] getQuestionInfo(Link link) {
        String url = link.getUrl();
        UriTemplate template = new UriTemplate("https://stackoverflow.com/questions/{id}/{title}");
        Map<String, String> variables = template.match(url);
        String[] info = new String[2];
        info[0] = variables.get("id");
        info[1] = variables.get("title");
        return info;
    }
}
