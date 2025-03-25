package backend.academy.scrapper.repository;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Link {
    @EqualsAndHashCode.Include
    private String url;

    private List<String> tags;
    private List<String> filters;
    private ZonedDateTime lastUpdated;
    private String siteName;
}
