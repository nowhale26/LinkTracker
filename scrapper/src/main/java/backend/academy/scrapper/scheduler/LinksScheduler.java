package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.botclient.BotClient;
import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.repository.LinkRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinksScheduler {

    @Autowired
    private SchedulerService service;

    @Autowired
    private BotClient botClient;

    @Autowired
    private LinkRepository repository;

    // fixedDelay = 3600000, initialDelay = 3600000 раз в час
    @Scheduled(fixedDelay = 60000, initialDelay = 0000) // fixedDelay = 60000, initialDelay = 60000 раз в минуту
    public void checkAllUpdates() {
        List<LinkUpdate> updates = service.findUpdatedLinks();
        Map<Long, Map<String, List<String>>> updatesByTag = new HashMap<>();

        if (updates != null) {
            for (var update : updates) {
                boolean tagEnabled =
                        repository.getUserByTgChatId(update.getTgChatId()).getEnableTagInUpdates();
                if (update.getTag() == null || !tagEnabled) {
                    try {
                        botClient.sendUpdate(update);
                    } catch (ScrapperException e) {
                        log.error("Error message: {}", e.getMessage());
                    }
                } else {
                    getUpdatesByTag(updatesByTag, update);
                }
            }
            sendUpdatesByTag(updatesByTag);
        }
    }

    private void getUpdatesByTag(Map<Long, Map<String, List<String>>> updatesByTag, LinkUpdate update) {
        Long tgChatId = update.getTgChatId();
        String tag = update.getTag();
        List<String> descriptions;
        Map<String, List<String>> tagsWithDescriptions;
        if (!updatesByTag.containsKey(tgChatId)) {
            tagsWithDescriptions = new HashMap<>();
            descriptions = new ArrayList<>();

        } else {
            tagsWithDescriptions = updatesByTag.get(tgChatId);
            if (!tagsWithDescriptions.containsKey(tag)) {
                descriptions = new ArrayList<>();
            } else {
                descriptions = tagsWithDescriptions.get(tag);
            }
        }
        descriptions.add(update.getDescription());
        tagsWithDescriptions.put(tag, descriptions);
        updatesByTag.put(tgChatId, tagsWithDescriptions);
    }

    private void sendUpdatesByTag(Map<Long, Map<String, List<String>>> updatesByTag) {
        for (var tagUpdateByUser : updatesByTag.entrySet()) {
            Long tgChatId = tagUpdateByUser.getKey();
            Map<String, List<String>> updatesWithTag = tagUpdateByUser.getValue();
            for (var updateWithTag : updatesWithTag.entrySet()) {
                LinkUpdate update = new LinkUpdate();
                update.setTgChatId(tgChatId);
                StringBuilder description = new StringBuilder("Обновления по тэгу: " + updateWithTag.getKey() + "\n\n");
                for (var linkUpdate : updateWithTag.getValue()) {
                    description.append(linkUpdate).append("\n\n");
                }
                update.setTag(updateWithTag.getKey());
                update.setId(0L);
                update.setDescription(description.toString());
                try {
                    botClient.sendUpdate(update);
                } catch (ScrapperException e) {
                    log.error("Error message in tag updates: {}", e.getMessage());
                }
            }
        }
    }
}
