package backend.academy.scrapper.externalapi.stackoverflow.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class StackoverflowAnswer {
    @JsonProperty("creation_date")
    private long creationDate; // Unix timestamp в секундах

    @JsonProperty("answer_id")
    private long answerId;

    @JsonProperty("body")
    private String body;

    @JsonProperty("owner")
    private StackoverflowOwner owner;

    public ZonedDateTime getCreationDateAsZonedDateTime() {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(creationDate), java.time.ZoneOffset.UTC);
    }
}
