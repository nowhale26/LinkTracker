package backend.academy.scrapper.externalapi.github.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GithubUser {
    @JsonProperty("login")
    private String login;
}
