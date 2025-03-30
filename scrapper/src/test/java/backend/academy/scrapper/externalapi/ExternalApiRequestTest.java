package backend.academy.scrapper.externalapi;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.links.LinksService;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.Repository;
import backend.academy.scrapper.repository.entity.Link;
import backend.academy.scrapper.scheduler.SchedulerService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExternalApiRequestTest extends BaseTest {

    private final SchedulerService schedulerService;
    private final LinksService linksService;
    private final LinkRepository repository;

    @Autowired
    public ExternalApiRequestTest(SchedulerService schedulerService, LinksService linksService, LinkRepository repository) {
        this.schedulerService = schedulerService;
        this.linksService = linksService;
        this.repository = repository;
    }

    @Test
    public void githubClientTest() {
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/def");
        linksService.addLink(3L, body);
        body.setLink("https://github.com/nowhale26/abc");
        linksService.addLink(4L, body);

        List<LinkUpdate> updates = schedulerService.findUpdatedLinks();
        for (var update : updates) {
            if (update.getTgChatId() == 4L) {
                assertThat(update.getUrl()).isEqualTo("https://github.com/nowhale26/abc");
            }
            assertThat(update.getTgChatId()).isNotEqualTo(3L);
        }
    }

    @Test
    public void stackoverflowClientTest() {
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://stackoverflow.com/questions/1/a");
        linksService.addLink(5L, body);
        body.setLink("https://stackoverflow.com/questions/2/b");
        linksService.addLink(6L, body);
        List<LinkUpdate> updates = schedulerService.findUpdatedLinks();
        for (var update : updates) {
            if (update.getTgChatId() == 5L) {
                assertThat(update.getUrl()).isEqualTo("https://stackoverflow.com/questions/1/a");
            }
            assertThat(update.getTgChatId()).isNotEqualTo(6L);
        }
    }
}
