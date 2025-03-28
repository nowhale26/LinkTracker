package backend.academy.scrapper.externalapi.stackoverflow.apirequest;

import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.externalapi.stackoverflow.StackoverflowClient;
import backend.academy.scrapper.externalapi.stackoverflow.StackoverflowService;
import backend.academy.scrapper.externalapi.stackoverflow.models.StackoverflowResponse;
import backend.academy.scrapper.repository.entity.Link;
import org.springframework.stereotype.Component;

@Component
public class CommentRequest implements StackoverflowApiRequest {
    private final StackoverflowClient stackoverflowClient;
    private final StackoverflowService stackoverflowService;

    public CommentRequest(StackoverflowClient stackoverflowClient, StackoverflowService stackoverflowService) {
        this.stackoverflowClient = stackoverflowClient;
        this.stackoverflowService = stackoverflowService;
    }

    @Override
    public ExternalApiResponse checkUpdate(Link link) {
        String[] info = getQuestionInfo(link);
        String questionId = info[0];
        String title = info[1];
        StackoverflowResponse response = stackoverflowClient.checkLinkUpdate(questionId, "comments");
        return stackoverflowService.apiResponse(response, title);
    }

    @Override
    public String formMessage(ExternalApiResponse response, Link link) {
        return """
            Ссылка: %s
            Тема вопроса: %s
            Пользователь: %s
            Комментарий: %s
            """.formatted(link.getUrl(), response.getMainInfo(), response.getUserName(), response.getPreview());
    }
}
