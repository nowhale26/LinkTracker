package backend.academy.scrapper.externalapi.github;

import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.externalapi.github.models.GithubResponse;
import org.springframework.stereotype.Component;

@Component
public class GithubService{

    private static final String siteName = "github";

    public ExternalApiResponse apiResponse(GithubResponse[] response) {
        ExternalApiResponse apiResponse = new ExternalApiResponse();
        if(response!=null){
            GithubResponse firstResponse = response[0];
            apiResponse.setMainInfo(firstResponse.getTitle());
            apiResponse.setUserName(firstResponse.getUser().getLogin());
            apiResponse.setCreatedAt(firstResponse.getCreatedAt());
            apiResponse.setPreview(firstResponse.getComment().substring(0,Math.min(200,firstResponse.getComment().length())));
        }
        return apiResponse;
    }
}
