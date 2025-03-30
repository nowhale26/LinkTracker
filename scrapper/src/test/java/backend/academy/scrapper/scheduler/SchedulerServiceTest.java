package backend.academy.scrapper.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.externalapi.github.GithubClient;
import backend.academy.scrapper.links.LinksService;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.repository.Repository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SchedulerServiceTest extends BaseTest {
    private final SchedulerService schedulerService;
    private final GithubClient githubClient;
    private final LinksService linksService;
    private final Repository repository;

    @Autowired
    public SchedulerServiceTest(
            SchedulerService schedulerService,
            GithubClient githubClient,
            LinksService linksService,
            Repository repository) {
        this.schedulerService = schedulerService;
        this.githubClient = githubClient;
        this.linksService = linksService;
        this.repository = repository;
    }

    @Test
    public void schedulerTest() {
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/fractalframes");
        linksService.addLink(2L, body);
        body.setLink("https://github.com/nowhale26/abc");
        linksService.addLink(1L, body);

        Map<String, List<Long>> links = schedulerService.findUpdatedLinks();
        assertThat(links.get("https://github.com/nowhale26/abc")).isNotEmpty().allSatisfy(item -> {
            assertThat(item).isEqualTo(1L);
        });
        repository.delete(1L);
    }
}
