package backend.academy.scrapper.controller;

import backend.academy.scrapper.controller.model.AddLinkRequest;
import backend.academy.scrapper.controller.model.LinkResponse;
import backend.academy.scrapper.controller.model.ListLinksResponse;
import backend.academy.scrapper.controller.model.RemoveLinkRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapperController implements LinksApi {

    @Override
    public ResponseEntity<LinkResponse> linksDelete(Long tgChatId, RemoveLinkRequest body) {
        return null;
    }

    @Override
    public ResponseEntity<ListLinksResponse> linksGet(Long tgChatId) {
        List<LinkResponse> links = List.of(new LinkResponse().url("https://github.com/central-university-dev/java-nowhale26"),
            new LinkResponse().url("https://stackoverflow.com/questions"));
        return new ResponseEntity<>(new ListLinksResponse().links(links), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LinkResponse> linksPost(Long tgChatId, AddLinkRequest body) {
        return null;
    }
}
