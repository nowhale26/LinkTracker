package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.externalapi.ExternalApiRequest;
import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.externalapi.github.GithubClient;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.entity.Link;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchedulerService {
    private final int pageSize = 50;

    @Autowired
    private LinkRepository repository;

    private final Map<String, List<ExternalApiRequest>> externalApiMap = new HashMap<>();

    public SchedulerService(List<ExternalApiRequest> externalApiRequestList) {
        for (var externalApi : externalApiRequestList) {
            if (externalApiMap.containsKey(externalApi.getSiteName())) {
                externalApiMap.get(externalApi.getSiteName()).add(externalApi);
            } else {
                List<ExternalApiRequest> externalApiList = new ArrayList<>();
                externalApiList.add(externalApi);
                externalApiMap.put(externalApi.getSiteName(), externalApiList);
            }
        }
    }

    @Transactional
    public List<LinkUpdate> findUpdatedLinks() {
        int pageNumber = 0;
        boolean hasNextPage = true;
        List<LinkUpdate> updates = new ArrayList<>();
        while (hasNextPage) {
            Page<Link> currentPage = repository.getPagedLinks(pageNumber + 1, pageSize);
            for (var link : currentPage.getContent()) {
                checkLinkUpdate(link, updates);
            }
            hasNextPage = currentPage.hasNext();
            pageNumber++;
        }

        return updates;
    }

    @Transactional
    public void checkLinkUpdate(Link link, List<LinkUpdate> updates) {
        boolean updated= false;
        for (var externalApi : externalApiMap.get(link.getSiteName())) {
            ExternalApiResponse response = externalApi.checkUpdate(link);
            if (response != null) {
                if(response.getCreatedAt().isAfter(link.getLastUpdated())){
                    LinkUpdate update = new LinkUpdate();
                    update.setId(link.getId());
                    update.setUrl(link.getUrl());
                    update.setDescription(externalApi.formMessage(response, link));
                    update.setTgChatIds(repository.getTgChatIdsByLink(link));
                    updates.add(update);
                    updated = true;
                }
            }
        }
        if(updated) {
            link.setLastUpdated(ZonedDateTime.now());
            Long tgchatId = repository.getTgChatIdById(link.getUserId());
            repository.save(tgchatId,link);
        }
    }
}
