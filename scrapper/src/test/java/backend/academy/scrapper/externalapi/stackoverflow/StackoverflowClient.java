package backend.academy.scrapper.externalapi.stackoverflow;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.externalapi.stackoverflow.models.StackoverflowAnswer;
import backend.academy.scrapper.externalapi.stackoverflow.models.StackoverflowOwner;
import backend.academy.scrapper.externalapi.stackoverflow.models.StackoverflowResponse;
import backend.academy.scrapper.repository.entity.Link;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
@Slf4j
public class StackoverflowClient{
    private final String siteName = "stackoverflow";


    public StackoverflowResponse checkLinkUpdate(String questionId, String type) {
        if ("1".equals(questionId)) {
            StackoverflowResponse response = new StackoverflowResponse();
            List<StackoverflowAnswer> answers = new ArrayList<>();
            StackoverflowAnswer answer = new StackoverflowAnswer();
            answer.setCreationDate(4102444800L);
            answer.setBody("test");
            answer.setAnswerId(1);
            StackoverflowOwner owner = new StackoverflowOwner();
            owner.setName("test");
            answer.setOwner(owner);
            answers.add(answer);
            response.setItems(answers);
            return response;
        } else if ("2".equals(questionId)) {
            throw new ScrapperException(siteName, "400", "Некорректный запрос");
        } else {
            StackoverflowResponse response = new StackoverflowResponse();
            List<StackoverflowAnswer> answers = new ArrayList<>();
            StackoverflowAnswer answer = new StackoverflowAnswer();
            answer.setCreationDate(800L);
            answer.setBody("test");
            answer.setAnswerId(2);
            StackoverflowOwner owner = new StackoverflowOwner();
            owner.setName("test");
            answer.setOwner(owner);
            answers.add(answer);
            response.setItems(answers);
            return response;
        }
    }


    public String getSiteName() {
        return siteName;
    }
}
