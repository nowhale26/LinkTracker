package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.externalapi.ExternalApiRequest;
import backend.academy.scrapper.externalapi.ExternalApiResponse;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.entity.Link;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchedulerService {
    private final int PAGE_SIZE = 1000;
    private final int THREADS = 4;

    @Qualifier("taskExecutor")
    @Autowired
    private TaskExecutor taskExecutor;

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
        List<LinkUpdate> updates = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger activeTasksCount = new AtomicInteger(0);
        while (hasNextPage) {
            Page<Link> currentPage = repository.getPagedLinks(pageNumber, PAGE_SIZE);
            List<Link> links = currentPage.getContent();
            activeTasksCount.addAndGet(links.size());

            final boolean isLastPage = !currentPage.hasNext();

            for (Link link : links) {
                taskExecutor.execute(() -> {
                    try {
                        processLink(link, updates);
                    } finally {
                        if (activeTasksCount.decrementAndGet() == 0 && isLastPage) {
                            latch.countDown();
                        }
                    }
                });
            }

            hasNextPage = currentPage.hasNext();
            pageNumber++;
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Ошибка выполнения многопоточного поиска обновления ссылок", e);
        }

        return updates;
    }

    private void processLink(Link link, List<LinkUpdate> updates) {
        boolean updated = false;
        for (var externalApi : externalApiMap.get(link.getSiteName())) {
            ExternalApiResponse response = externalApi.checkUpdate(link);
            if (response != null && response.getCreatedAt().isAfter(link.getLastUpdated())) {
                LinkUpdate update = new LinkUpdate();
                update.setId(link.getId());
                update.setUrl(link.getUrl());
                update.setDescription(externalApi.formMessage(response, link));
                update.setTgChatId(repository.getTgChatIdByLink(link));
                if (!link.getTags().isEmpty()) {
                    update.setTag(link.getTags().iterator().next().getTag());
                }
                updates.add(update);
                updated = true;
            }
        }
        if (updated) {
            link.setLastUpdated(ZonedDateTime.now());
            Long tgChatId = repository.getTgChatIdById(link.getUserId());
            repository.save(tgChatId, link);
        }
    }
}
