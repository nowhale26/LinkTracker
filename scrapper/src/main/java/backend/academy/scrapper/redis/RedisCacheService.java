package backend.academy.scrapper.redis;

import backend.academy.scrapper.links.model.ListLinksResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long CACHE_TTL = 3600; // Время жизни кеша в секундах (1 час)

    @Autowired
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
        return Boolean.TRUE.equals(redisTemplate.hasKey(generateCacheKey(tgChatId)));
    }


    public void invalidateCache(Long tgChatId) {
        redisTemplate.delete(generateCacheKey(tgChatId));
    }
}
