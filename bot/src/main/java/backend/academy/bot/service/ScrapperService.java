package backend.academy.bot.service;

import backend.academy.bot.service.model.AddLinkRequest;
import backend.academy.bot.service.model.LinkResponse;
import backend.academy.bot.service.model.ListLinksResponse;
import backend.academy.bot.service.model.RemoveLinkRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScrapperService {

    public ListLinksResponse getLinks(Long userId) {
        List<LinkResponse> links = List.of(new LinkResponse().url("https://github.com/central-university-dev/java-nowhale26"),
            new LinkResponse().url("https://stackoverflow.com/questions"));
        return new ListLinksResponse().links(links);
    }

    public void addLink(Long userId, AddLinkRequest link) {

    }

    public void removeLink(Long userId, RemoveLinkRequest link) {

    }

}
