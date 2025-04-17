package backend.academy.scrapper.links.model;

import lombok.Data;
import java.time.LocalTime;

@Data
public class EnableDigestRequest {
    private LocalTime digestTime;
    private boolean enableDigest;
}
