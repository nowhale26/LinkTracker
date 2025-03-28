package backend.academy.scrapper.externalapi;

import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class ExternalApiResponse {
    private String mainInfo;
    private String userName;
    private String preview;
    private ZonedDateTime createdAt;
}
