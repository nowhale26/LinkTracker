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
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SchedulerService {

    @Autowired
    GithubClient githubClient;

    @Autowired
    Repository repository;

    private final Map<String, ExternalApi> externalApiMap = new HashMap<>();

    public SchedulerService(List<ExternalApi> externalApiList) {
        for (var externalApi : externalApiList) {
            externalApiMap.put(externalApi.getSiteName(), externalApi);
        }

    }

    public HashMap<String, List<Long>> findUpdatedLinks() {
        HashMap<Long, Set<Link>> linksRepository = repository.getRepository();
        if (linksRepository == null) {
            return null;
        }
        HashMap<String, List<Long>> updatedLinks = new HashMap<>();
        for (var linksEntry : linksRepository.entrySet()) {
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
                    if (update != null) {
                        if (update.isAfter(link.getLastUpdated())) {
                            link.setLastUpdated(update);
                            repository.save(linksEntry.getKey(), link);
                            List<Long> iDs;
                            if (updatedLinks.containsKey(link.getUrl())) {
                                iDs = updatedLinks.get(link.getUrl());
                            } else {
                                iDs = new ArrayList<>();
                            }
                            iDs.add(linksEntry.getKey());
                            updatedLinks.put(link.getUrl(), iDs);
                        }
                    }
                }

            }
        }
        return updatedLinks;
    }

}
