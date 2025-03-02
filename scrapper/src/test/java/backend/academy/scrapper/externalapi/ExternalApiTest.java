package backend.academy.scrapper.externalapi;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.links.LinksService;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.repository.Repository;
import backend.academy.scrapper.scheduler.SchedulerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class ExternalApiTest extends BaseTest {

    private final SchedulerService schedulerService;
    private final LinksService linksService;
    private final Repository repository;

    @Autowired
    public ExternalApiTest(SchedulerService schedulerService, LinksService linksService, Repository repository) {
        this.schedulerService = schedulerService;
        this.linksService = linksService;
        this.repository = repository;
    }

    @Test
    public void githubClientTest(){
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/def");
        linksService.addLink(3L,body);
        body.setLink("https://github.com/nowhale26/abc");
        linksService.addLink(4L, body);

        Map<String, List<Long>> links = schedulerService.findUpdatedLinks();
        assertThat(links.get("https://github.com/nowhale26/abc")).isNotEmpty().allSatisfy(item -> {
            assertThat(item).isEqualTo(4L);
        });
        repository.delete(4L);
    }

    @Test
    public void stackoverflowClientTest(){
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://stackoverflow.com/questions/1/a");
        linksService.addLink(5L,body);
        body.setLink("https://stackoverflow.com/questions/2/b");
        linksService.addLink(6L, body);

        Map<String, List<Long>> links = schedulerService.findUpdatedLinks();
        assertThat(links.get("https://stackoverflow.com/questions/1/a")).isNotEmpty().allSatisfy(item -> {
            assertThat(item).isEqualTo(5L);
        });
        repository.delete(5L);
    }
}
