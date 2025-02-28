package backend.academy.scrapper.externalapi.stackoverflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class StackoverflowClient {
    private final WebClient stackoverflowWebClient;

    public StackoverflowClient(WebClient stackoverflowWebClient) {
        this.stackoverflowWebClient = stackoverflowWebClient;
    }

}
