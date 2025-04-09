package backend.academy.scrapper.scheduler;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.botclient.model.LinkUpdate;
import backend.academy.scrapper.links.LinksService;
import backend.academy.scrapper.links.model.AddLinkRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SchedulerServiceTest extends BaseTest {
    private final SchedulerService schedulerService;
    private final LinksService linksService;

    @Autowired
    public SchedulerServiceTest(SchedulerService schedulerService, LinksService linksService) {
        this.schedulerService = schedulerService;
        this.linksService = linksService;
    }

    @Test
    public void schedulerTest() {
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/fractalframes");
        linksService.addLink(2L, body);
        body.setLink("https://github.com/nowhale26/abc");
        linksService.addLink(1L, body);

        List<LinkUpdate> updates = schedulerService.findUpdatedLinks();
        for (var update : updates) {
            if (update.getTgChatId() == 1L) {
                assertThat(update.getUrl()).isEqualTo("https://github.com/nowhale26/abc");
            }
        }
    }
}
