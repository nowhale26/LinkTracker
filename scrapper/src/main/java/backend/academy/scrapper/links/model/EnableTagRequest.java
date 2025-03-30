package backend.academy.scrapper.links.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EnableTagRequest {
    @JsonProperty("enable")
    private boolean enableTagInUpdates;
}
