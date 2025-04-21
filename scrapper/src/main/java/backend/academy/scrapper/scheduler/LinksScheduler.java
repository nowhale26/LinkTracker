package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.botclient.UpdateSender;
import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.entity.User;
import jakarta.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinksScheduler {

    @Autowired
    private SchedulerService service;

    @Autowired
    private UpdateSender updateSender;

    @Autowired
    private LinkRepository repository;

    private final Map<Long, ScheduledFuture<?>> userScheduledTasks = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_DELAYED_UPDATES_KEY = "delayed_updates:";

    public LinksScheduler(TaskScheduler taskScheduler, RedisTemplate<String, Object> redisTemplate) {
        this.taskScheduler = taskScheduler;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void initSchedules() {
        Set<User> digestUsers = repository.getByEnabledDigest();
        digestUsers.parallelStream().forEach(this::scheduleDigestFor);
    }

    @Scheduled(fixedDelay = 3600000) // Проверка раз в час
    public void checkForUserSettingsChanges() {
        Set<User> digestUsers = repository.getByEnabledDigest();
        Set<Long> currentUserIds = digestUsers.stream().map(User::getTgChatId).collect(Collectors.toSet());

        Set<Long> scheduledUsers = new HashSet<>(userScheduledTasks.keySet());
        for (Long userId : scheduledUsers) {
            if (!currentUserIds.contains(userId)) {
                cancelScheduleForUser(userId);
            }
        }

        for (User user : digestUsers) {
            scheduleDigestFor(user);
        }
    }

    private void scheduleDigestFor(User user) {
        try {
            long chatId = user.getTgChatId();
            LocalTime digestTime = user.getDigestTime();

            cancelScheduleForUser(chatId);

            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime nextRun = now.with(LocalTime.of(digestTime.getHour(), digestTime.getMinute()));

            if (now.isAfter(nextRun)) {
                nextRun = nextRun.plusDays(1);
            }

            ScheduledFuture<?> future = taskScheduler.schedule(() -> processDailyDigest(user), nextRun.toInstant());

            userScheduledTasks.put(chatId, future);
        } catch (Exception e) {
            log.error("Failed to schedule digest for user {}: {}", user.getTgChatId(), e.getMessage(), e);
        }
    }

    private void processDailyDigest(User user) {
        long chatId = user.getTgChatId();
        String key = REDIS_DELAYED_UPDATES_KEY + chatId;

        List<LinkUpdate> updates = new ArrayList<>();
        while (true) {
            LinkUpdate update = (LinkUpdate) redisTemplate.opsForList().leftPop(key);
            if (update == null) {
                break;
            }
            updates.add(update);
        }
        sendUpdates(updates);
    }

    private void cancelScheduleForUser(long chatId) {
        ScheduledFuture<?> existingTask = userScheduledTasks.get(chatId);
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel(false);
            userScheduledTasks.remove(chatId);
        }
    }

    // fixedDelay = 3600000, initialDelay = 3600000 раз в час
    @Scheduled(fixedDelay = 60000, initialDelay = 0000) // fixedDelay = 60000, initialDelay = 60000 раз в минуту
    public void checkAllUpdates() {
        List<LinkUpdate> updates = service.findUpdatedLinks();
        List<LinkUpdate> updatesWithoutDigest = new ArrayList<>();
        if (updates != null) {
            for (var update : updates) {
                if (!userScheduledTasks.containsKey(update.getTgChatId())) {
                    updatesWithoutDigest.add(update);
                } else {
                    String key = REDIS_DELAYED_UPDATES_KEY + update.getTgChatId();
                    redisTemplate.opsForList().rightPush(key, update);
                }
            }
            sendUpdates(updatesWithoutDigest);
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
                    updateSender.sendUpdate(update);
                } catch (ScrapperException e) {
                    log.error("Error message in tag updates: {}", e.getMessage());
                }
            }
        }
    }

    private void sendUpdates(List<LinkUpdate> updates) {
        Map<Long, Map<String, List<String>>> updatesByTag = new HashMap<>();
        for (var update : updates) {
            boolean tagEnabled =
                    repository.getUserByTgChatId(update.getTgChatId()).getEnableTagInUpdates();
            if (update.getTag() == null || !tagEnabled) {
                try {
                    updateSender.sendUpdate(update);
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
