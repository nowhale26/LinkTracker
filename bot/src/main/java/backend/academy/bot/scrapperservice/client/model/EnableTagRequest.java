package backend.academy.bot.scrapperservice.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.tomcat.Jar;

@Data
public class EnableTagRequest {
    @JsonProperty("enable")
    private boolean enableTagInUpdates;
}
