package backend.academy.scrapper.redis;


import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.links.LinksService;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.links.model.RemoveLinkRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class RedisTest extends BaseTest {

    @Autowired
    private RedisCacheService cacheService;

    @Autowired
    private LinksService linksService;

    @Container
    private static final GenericContainer<?> redisContainer =
        new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        redisContainer.start();
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }


    @Test
    void testListCommandCache() {
        AddLinkRequest link = new AddLinkRequest();
        link.setLink("https://stackoverflow.com/questions/42486428/when-should-i-use-streams");
        linksService.addLink(1L, link);
        linksService.getLinks(1L);
        String cacheLink = cacheService.getUserLinksFromCache(1L).getLinks().getFirst().getUrl();
        assertThat(cacheLink).isEqualTo("https://stackoverflow.com/questions/42486428/when-should-i-use-streams");

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest();
        removeLinkRequest.setLink("https://stackoverflow.com/questions/42486428/when-should-i-use-streams");
        linksService.deleteLink(1L, removeLinkRequest);
        assertThat(cacheService.hasCachedLinks(1L)).isFalse();
    }

}
