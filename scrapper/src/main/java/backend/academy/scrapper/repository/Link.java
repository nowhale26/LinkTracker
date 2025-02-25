package backend.academy.scrapper.repository;

import backend.academy.scrapper.links.model.AddLinkRequest;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Objects;

@Data
public class Link {
    private String url;
    private List<String> tags;
    private List<String> filters;

}
