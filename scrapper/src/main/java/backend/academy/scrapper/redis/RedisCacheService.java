package backend.academy.scrapper.redis;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.links.model.ListLinksResponse;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final long CACHE_TTL; // Время жизни кеша в секундах

    @Autowired
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ScrapperConfig config) {
        this.redisTemplate = redisTemplate;
        CACHE_TTL = Long.parseLong(config.cacheTtl());
    }

    public String generateCacheKey(Long tgChatId) {
        return "user_links:" + tgChatId;
    }

    public void cacheUserLinks(Long tgChatId, ListLinksResponse links) {
        redisTemplate.opsForValue().set(generateCacheKey(tgChatId), links, CACHE_TTL, TimeUnit.SECONDS);
    }

    public ListLinksResponse getUserLinksFromCache(Long tgChatId) {
        return (ListLinksResponse) redisTemplate.opsForValue().get(generateCacheKey(tgChatId));
    }

    public boolean hasCachedLinks(Long tgChatId) {
        return redisTemplate.hasKey(generateCacheKey(tgChatId));
    }

    public void invalidateCache(Long tgChatId) {
        redisTemplate.delete(generateCacheKey(tgChatId));
    }
}
