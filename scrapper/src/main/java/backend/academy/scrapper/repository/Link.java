package backend.academy.scrapper.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Link {
    private String url;
    private List<String> tags;
    private List<String> filters;
    private ZonedDateTime lastUpdated;
    private String siteName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }
}
