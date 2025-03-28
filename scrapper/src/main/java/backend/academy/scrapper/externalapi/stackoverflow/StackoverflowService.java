package backend.academy.scrapper.externalapi.stackoverflow;

import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.externalapi.stackoverflow.models.StackoverflowAnswer;
import backend.academy.scrapper.externalapi.stackoverflow.models.StackoverflowResponse;
import org.springframework.stereotype.Service;

@Service
public class StackoverflowService {
    private static final String siteName = "stackoverflow";

    public ExternalApiResponse apiResponse(StackoverflowResponse response, String title) {
        ExternalApiResponse apiResponse = new ExternalApiResponse();
        if (response != null) {
            StackoverflowAnswer answer = response.getItems().getFirst();
            apiResponse.setMainInfo(title);
            apiResponse.setUserName(answer.getOwner().getName());
            apiResponse.setCreatedAt(answer.getCreationDateAsZonedDateTime());
            apiResponse.setPreview(answer.getBody().substring(0, Math.min(answer.getBody().length(), 200)));
        }
        return apiResponse;
    }


}
