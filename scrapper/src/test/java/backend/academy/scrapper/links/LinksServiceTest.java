package backend.academy.scrapper.links;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.links.model.LinkResponse;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.entity.Link;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LinksServiceTest extends BaseTest {
    private final LinksService linksService;
    private final LinkRepository repository;

    @Autowired
    public LinksServiceTest(LinksService linksService, LinkRepository repository) {
        this.linksService = linksService;
        this.repository = repository;
    }

    @Test
    public void addLinkValidationTest() {
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/loganalyzer");
        LinkResponse linkResponse = linksService.addLink(123L, body);
        assertThat(linkResponse).isNotNull();

        body.setLink(
                "https://stackoverflow.com/questions/60968250/i-use-grpc-to-generate-java-code-javax-annotation-generated-and-it-reports-e/75368453#75368453");
        try {
            linksService.addLink(123L, body);
            failBecauseExceptionWasNotThrown(BusinessException.class);
        } catch (BusinessException e) {
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка");
        }

        body.setLink("https://github.com/nowhale26/fractalframes/issues");
        try {
            linksService.addLink(123L, body);
            failBecauseExceptionWasNotThrown(BusinessException.class);
        } catch (BusinessException e) {
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка");
        }

        body.setLink("abc");
        try {
            linksService.addLink(123L, body);
            failBecauseExceptionWasNotThrown(BusinessException.class);
        } catch (BusinessException e) {
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка");
        }
    }

    @Test
    public void addLinkContentTest() {
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/loganalyzer2");
        body.setTags(List.of("work"));
        body.setFilters(List.of("comment:dummy"));
        linksService.addLink(124L, body);
        List<Link> links = repository.getAllLinks();
        for (var link : links) {
            if (link.getUrl().equals("https://github.com/nowhale26/loganalyzer2")) {
                assertThat("https://github.com/nowhale26/loganalyzer2").isEqualTo(link.getUrl());
                assertThat("work")
                        .isEqualTo(link.getTags().stream().toList().getFirst().getTag());
                assertThat("comment:dummy")
                        .isEqualTo(
                                link.getFilters().stream().toList().getFirst().getFilter());
            }
        }
    }

    @Test
    public void addDuplicateLinkTest() {
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/loganalyzer");
        body.setTags(List.of("work", "hobby"));
        body.setFilters(List.of("comment:dummy", "job:work"));
        linksService.addLink(125L, body);
        body.setLink("https://github.com/nowhale26/loganalyzer");
        body.setTags(List.of("football"));
        body.setFilters(List.of("user:dummy"));
        linksService.addLink(125L, body);
        List<Link> links = repository.getAllLinks();
        for (var link : links) {
            if (link.getUrl().equals("https://github.com/nowhale26/loganalyzer")) {
                assertThat("football")
                        .isEqualTo(link.getTags().stream().toList().getFirst().getTag());
                assertThat("user:dummy")
                        .isEqualTo(
                                link.getFilters().stream().toList().getFirst().getFilter());
            }
        }
    }
}
