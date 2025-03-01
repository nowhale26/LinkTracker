package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.externalapi.github.GithubClient;
import backend.academy.scrapper.links.LinksService;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.links.model.LinkResponse;
import backend.academy.scrapper.repository.Link;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class SchedulerServiceTest extends BaseTest {
    private final SchedulerService schedulerService;
    private final GithubClient githubClient;
    private final LinksService linksService;

    @Autowired
    public SchedulerServiceTest(SchedulerService schedulerService, GithubClient githubClient, LinksService linksService) {
        this.schedulerService = schedulerService;
        this.githubClient = githubClient;
        this.linksService = linksService;
    }

    @Test
    public void schedulerTest(){
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/fractalframes");
        linksService.addLink(2L,body);
        body.setLink("https://github.com/nowhale26/abc");
        linksService.addLink(1L, body);

        Link link = new Link();

        Map<String, List<Long>> links = schedulerService.findUpdatedLinks();
        assertThat(links.get("https://github.com/nowhale26/abc")).isNotEmpty().allSatisfy(item -> {
            assertThat(item).isEqualTo(1L);
        });
    }
}
