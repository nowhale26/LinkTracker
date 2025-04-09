package backend.academy.scrapper.externalapi;

import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class ExternalApiResponse {
    private String mainInfo;
    private String userName;
    private String preview;
    private ZonedDateTime createdAt;
}
