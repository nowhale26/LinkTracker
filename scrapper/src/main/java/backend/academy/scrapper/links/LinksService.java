package backend.academy.scrapper.links;

import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.common.validator.LinkValidator;
import backend.academy.scrapper.kafka.KafkaDLQService;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.links.model.LinkResponse;
import backend.academy.scrapper.links.model.ListLinksResponse;
import backend.academy.scrapper.links.model.RemoveLinkRequest;
import backend.academy.scrapper.redis.RedisCacheService;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.entity.Filter;
import backend.academy.scrapper.repository.entity.Link;
import backend.academy.scrapper.repository.entity.Tag;
import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinksService {

    @Autowired
    private LinkRepository repository;

    @Autowired
    private LinkValidator validator;

    @Autowired
    private KafkaDLQService kafkaDLQService;

    private final RedisCacheService cacheService;

    public LinksService(RedisCacheService cacheService) {
        this.cacheService = cacheService;
    }

    public LinkResponse addLink(Long tgChatId, AddLinkRequest body) {
        Link link = new Link();
        link.setUrl(body.getLink());
        Set<Tag> tags = new HashSet<>();
        if (body.getTags() != null) {
            for (String stringTag : body.getTags()) {
                Tag tag = new Tag();
                tag.setTag(stringTag);
                tags.add(tag);
            }
        }
        link.setTags(tags);
        Set<Filter> filters = new HashSet<>();
        if (body.getFilters() != null) {
            for (String stringFilter : body.getFilters()) {
                Filter filter = new Filter();
                filter.setFilter(stringFilter);
                filters.add(filter);
            }
        }
        link.setFilters(filters);
        link.setLastUpdated(ZonedDateTime.of(2010, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()));
        //link.setLastUpdated(ZonedDateTime.now());
        extractSiteName(link);
        try{
            validator.validateLink(link);
        } catch (BusinessException e) {
            kafkaDLQService.sendToDLQ(body);
            throw e;
        }
        repository.save(tgChatId, link);
        cacheService.invalidateCache(tgChatId);
        return createResponse(tgChatId, link, body.getFilters(), body.getTags());
    }

    public LinkResponse deleteLink(Long tgChatId, RemoveLinkRequest body) {
        Link link = new Link();
        link.setUrl(body.getLink());
        repository.delete(tgChatId, link);
        cacheService.invalidateCache(tgChatId);
        return createResponse(tgChatId, link, null, null);
    }

    public ListLinksResponse getLinks(Long tgChatId) {

        if (cacheService.hasCachedLinks(tgChatId)) {
            return cacheService.getUserLinksFromCache(tgChatId);
        }

        Set<Link> links = repository.get(tgChatId);
        if (links == null) {
            throw new BusinessException(
                    "Пользователь с таким id не зарегестрирован", "400", "Некорректный id пользователя");
        } else if (links.isEmpty()) {
            throw new BusinessException("У данного пользователя нет ни одной ссылки", "400", "Пустой массив ссылок");
        }
        ListLinksResponse listLinksResponse = new ListLinksResponse();
        List<LinkResponse> linkResponseList = new ArrayList<>();
        for (Link link : links) {
            linkResponseList.add(new LinkResponse().url(link.getUrl()));
        }
        listLinksResponse.setLinks(linkResponseList);

        cacheService.cacheUserLinks(tgChatId, listLinksResponse);

        return listLinksResponse;
    }

    public void registerChat(Long id) {
        repository.register(id);
    }

    public void deleteChat(Long id) {
        repository.delete(id);
    }

    public void enableTagInUpdates(Long tgChatId, boolean enableTagInUpdates) {
        repository.save(tgChatId, enableTagInUpdates);
    }

    private LinkResponse createResponse(Long userId, Link link, List<String> filters, List<String> tags) {
        LinkResponse linkResponse = new LinkResponse();
        linkResponse.setId(userId);
        linkResponse.setFilters(filters);
        linkResponse.setTags(tags);
        linkResponse.setUrl(link.getUrl());
        return linkResponse;
    }

    private void extractSiteName(Link link) {
        String urlString = link.getUrl();
        try {
            URI uri = new URI(urlString);
            String host = uri.getHost();
            String[] parts = host.split("\\.");
            link.setSiteName(parts[parts.length - 2]);
        } catch (Exception e) {
            throw new BusinessException("Некорректная ссылка", "400", e.getMessage());
        }
    }
}
