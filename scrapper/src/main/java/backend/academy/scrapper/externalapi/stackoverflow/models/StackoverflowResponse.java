package backend.academy.scrapper.externalapi.stackoverflow.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class StackoverflowResponse {
    @JsonProperty("items")
    private List<StackoverflowAnswer> items;
}
