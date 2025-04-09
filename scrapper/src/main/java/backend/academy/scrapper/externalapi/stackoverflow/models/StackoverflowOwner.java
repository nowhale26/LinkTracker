package backend.academy.scrapper.externalapi.stackoverflow.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StackoverflowOwner {
    @JsonProperty("display_name")
    private String name;
}
