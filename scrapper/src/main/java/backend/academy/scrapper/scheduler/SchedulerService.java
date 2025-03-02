package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.externalapi.ExternalApi;
import backend.academy.scrapper.externalapi.github.GithubClient;
import backend.academy.scrapper.repository.Link;
import backend.academy.scrapper.repository.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SchedulerService {

    @Autowired
    private GithubClient githubClient;

    @Autowired
    private Repository repository;

    private final Map<String, ExternalApi> externalApiMap = new HashMap<>();

    public SchedulerService(List<ExternalApi> externalApiList) {
        for (var externalApi : externalApiList) {
            externalApiMap.put(externalApi.getSiteName(), externalApi);
        }

    }

    public Map<String, List<Long>> findUpdatedLinks() {
        Map<Long, Set<Link>> linksRepository = repository.getRepository();
        if (linksRepository == null) {
            return null;
        }
        Map<String, List<Long>> updatedLinks = new HashMap<>();
        Map<Long, Set<Link>> linksToUpdate = new HashMap<>(); // Временное хранилище для обновлений

        for (var linksEntry : linksRepository.entrySet()) {
            Set<Link> updatedSet = new HashSet<>(); // Копия для обновлений
            for (var link : linksEntry.getValue()) {
                ZonedDateTime update;
                String siteName = link.getSiteName();
                if (siteName != null) {
                    try {
                        update = externalApiMap.get(siteName).checkLinkUpdate(link);
                    } catch (ScrapperException e) {
                        log.error(e.getMessage());
                        continue;
                    } catch (NullPointerException e) {
                        log.error("Неправильное имя сайта");
                        continue;
                    }
                    if (update != null && update.isAfter(link.getLastUpdated())) {
                        link.setLastUpdated(update);
                        updatedSet.add(link); // Сохраняем во временный набор
                        List<Long> iDs = updatedLinks.computeIfAbsent(link.getUrl(), k -> new ArrayList<>());
                        iDs.add(linksEntry.getKey());
                    }
                }
            }
            if (!updatedSet.isEmpty()) {
                linksToUpdate.put(linksEntry.getKey(), updatedSet);
            }
        }

        for (var entry : linksToUpdate.entrySet()) {
            repository.save(entry.getKey(), entry.getValue().iterator().next());
        }

        return updatedLinks;
    }

}
