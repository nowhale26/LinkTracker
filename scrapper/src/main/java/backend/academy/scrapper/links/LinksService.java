package backend.academy.scrapper.links;

import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.links.model.LinkResponse;
import backend.academy.scrapper.links.model.ListLinksResponse;
import backend.academy.scrapper.links.model.RemoveLinkRequest;
import backend.academy.scrapper.repository.Link;
import backend.academy.scrapper.repository.Repository;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinksService {

    @Autowired
    private Repository repository;

    public LinkResponse addLink(Long userId, AddLinkRequest body) {
        Link link = new Link();
        link.setUrl(body.getLink());
        link.setTags(body.getTags());
        link.setFilters(body.getFilters());
        link.setLastUpdated(ZonedDateTime.now());
        validateURL(link);
        repository.save(userId, link);
        return createResponse(userId, link);
    }

    public LinkResponse deleteLink(Long userId, RemoveLinkRequest body) {
        Link link = new Link();
        link.setUrl(body.getLink());
        repository.delete(userId, link);
        return createResponse(userId, link);
    }

    public ListLinksResponse getLinks(Long userId) {
        Set<Link> links = repository.get(userId);
        if (links == null) {
            throw new BusinessException("Пользователь с таким id не зарегестрирован", "400", "Некорректный id пользователя");
        } else if (links.isEmpty()) {
            throw new BusinessException("У данного пользователя нет ни одной ссылки", "400", "Пустой массив ссылок");
        }
        ListLinksResponse listLinksResponse = new ListLinksResponse();
        List<LinkResponse> linkResponseList = new ArrayList<>();
        for (Link link : links) {
            linkResponseList.add(new LinkResponse().url(link.getUrl()));
        }
        listLinksResponse.setLinks(linkResponseList);
        return listLinksResponse;
    }

    public void registerChat(Long id) {
        repository.register(id);
    }

    public void deleteChat(Long id) {
        repository.delete(id);
    }

    private void validateURL(Link link) {
        try {
            new URI(link.getUrl());
        } catch (URISyntaxException e) {
            throw new BusinessException("Ссылка с некорректным синтаксисом", "400", "Некорректная ссылка");
        }
    }

    private LinkResponse createResponse(Long userId, Link link) {
        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setId(userId);
        linkResponse.setFilters(link.getFilters());
        linkResponse.setTags(link.getTags());
        linkResponse.setUrl(link.getUrl());
        return linkResponse;
    }


}
