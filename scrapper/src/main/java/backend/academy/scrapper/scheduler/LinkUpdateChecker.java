package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.externalapi.ExternalApiRequest;
import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.entity.Link;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LinkUpdateChecker implements Runnable {
    private Link link;

    private final Map<String, List<ExternalApiRequest>> externalApiMap;
    private final LinkRepository repository;
    private final List<LinkUpdate> updates;

    @Transactional
    @Override
    public void run() {
        boolean updated= false;
        for (var externalApi : externalApiMap.get(link.getSiteName())) {
            ExternalApiResponse response = externalApi.checkUpdate(link);
            if (response != null) {
                if(response.getCreatedAt().isAfter(link.getLastUpdated())) {
                    LinkUpdate update = new LinkUpdate();
                    update.setId(link.getId());
                    update.setUrl(link.getUrl());
                    update.setDescription(externalApi.formMessage(response, link));
                    update.setTgChatId(repository.getTgChatIdByLink(link));
                    if(!link.getTags().isEmpty()){
                        update.setTag(link.getTags().iterator().next().getTag());
                    }
                    updates.add(update);
                    updated = true;
                }
            }
        }
        if(updated) {
            link.setLastUpdated(ZonedDateTime.now());
            Long tgChatId = repository.getTgChatIdById(link.getUserId());
            repository.save(tgChatId,link);
        }
    }
}
