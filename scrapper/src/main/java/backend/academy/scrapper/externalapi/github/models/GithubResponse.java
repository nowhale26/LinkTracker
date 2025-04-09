package backend.academy.scrapper.externalapi.github.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class GithubResponse {
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("user")
    private GithubUser user;

    @JsonProperty("body")
    private String comment;
}
