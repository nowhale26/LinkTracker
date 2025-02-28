package backend.academy.scrapper.externalapi.stackoverflow.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class StackoverflowResponse {
    @JsonProperty("items")
    private List<StackoverflowAnswer> items;
}
