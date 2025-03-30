package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.externalapi.ExternalApiRequest;
import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.entity.Link;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchedulerService {
    private final int PAGE_SIZE = 1000;
    private final int THREADS = 4;

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

    public List<LinkUpdate> findUpdatedLinks() {
        int pageNumber = 0;
        boolean hasNextPage = true;
        List<LinkUpdate> updates = new ArrayList<>();
        try (ExecutorService executor = Executors.newFixedThreadPool(THREADS)) {
            while (hasNextPage) {
                Page<Link> currentPage = repository.getPagedLinks(pageNumber, PAGE_SIZE);
                List<Link> links = currentPage.getContent();
                for (var link : links) {
                    LinkUpdateChecker checker = new LinkUpdateChecker(link, externalApiMap, repository, updates);
                    executor.submit(checker::run);
                }
                hasNextPage = currentPage.hasNext();
                pageNumber++;
            }
            executor.shutdown();
        }

        return updates;
    }

}
