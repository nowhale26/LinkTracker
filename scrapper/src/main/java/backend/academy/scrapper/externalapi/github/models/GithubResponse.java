package backend.academy.scrapper.externalapi.github.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class GithubResponse {
    @JsonProperty("name")
    private String name;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    @JsonProperty("date")
    private ZonedDateTime date;

}
