package backend.academy.scrapper.links;

import backend.academy.scrapper.externalapi.github.apirequest.PRRequest;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.links.model.LinkResponse;
import backend.academy.scrapper.links.model.LinksApi;
import backend.academy.scrapper.links.model.ListLinksResponse;
import backend.academy.scrapper.links.model.RemoveLinkRequest;
import backend.academy.scrapper.links.model.TgChatApi;
import backend.academy.scrapper.repository.entity.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinksController implements LinksApi, TgChatApi {

    @Autowired
    private LinksService linksService;

    @Override
    public ResponseEntity<LinkResponse> linksDelete(Long tgChatId, RemoveLinkRequest body) {
        LinkResponse response = linksService.deleteLink(tgChatId, body);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ListLinksResponse> linksGet(Long tgChatId) {
        ListLinksResponse response = linksService.getLinks(tgChatId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<LinkResponse> linksPost(Long tgChatId, AddLinkRequest body) {
        LinkResponse response = linksService.addLink(tgChatId, body);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> tgChatIdDelete(Long id) {
        linksService.deleteChat(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> tgChatIdPost(Long id) {
        linksService.registerChat(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
