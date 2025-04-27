package backend.academy.scrapper.links.model;

import java.time.LocalTime;
import lombok.Data;

@Data
public class EnableDigestRequest {
    private LocalTime digestTime;
    private boolean enableDigest;
}
