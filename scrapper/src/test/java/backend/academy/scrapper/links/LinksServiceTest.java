package backend.academy.scrapper.links;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.links.model.LinkResponse;
import backend.academy.scrapper.repository.Link;
import backend.academy.scrapper.repository.Repository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;


public class LinksServiceTest extends BaseTest {
    private final LinksService linksService;
    private final Repository repository;

    @Autowired
    public LinksServiceTest(LinksService linksService, Repository repository) {
        this.linksService = linksService;
        this.repository = repository;
    }


    @Test
    public void addLinkValidationTest() {
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/loganalyzer");
        LinkResponse linkResponse = linksService.addLink(123L, body);
        assertThat(linkResponse).isNotNull();

        body.setLink("https://stackoverflow.com/questions/60968250/i-use-grpc-to-generate-java-code-javax-annotation-generated-and-it-reports-e/75368453#75368453");
        try {
            linksService.addLink(123L,body);
            failBecauseExceptionWasNotThrown(BusinessException.class);
        } catch (BusinessException e) {
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка");
        }

        body.setLink("https://github.com/nowhale26/fractalframes/issues");
        try {
            linksService.addLink(123L,body);
            failBecauseExceptionWasNotThrown(BusinessException.class);
        } catch (BusinessException e) {
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка");
        }

        body.setLink("abc");
        try {
            linksService.addLink(123L,body);
            failBecauseExceptionWasNotThrown(BusinessException.class);
        } catch (BusinessException e) {
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка");
        }
    }

    @Test
    public void addLinkContentTest(){
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/loganalyzer");
        body.setTags(List.of("work","hobby"));
        body.setFilters(List.of("comment:dummy","job:work"));
        linksService.addLink(124L, body);
        Set<Link> links = repository.getRepository().get(124L);
        Link linktest = new Link();
        for(var link : links){
            assertThat("https://github.com/nowhale26/loganalyzer")
                .isEqualTo(link.getUrl());
            assertThat(List.of("work","hobby"))
                .isEqualTo(link.getTags());
            assertThat(List.of("comment:dummy","job:work"))
                .isEqualTo(link.getFilters());
            linktest=link;
        }

        repository.delete(124L,linktest);
        assertThat(repository.get(124L).size()).isEqualTo(0);
    }

    @Test
    public void addDuplicateLinkTest(){
        AddLinkRequest body = new AddLinkRequest();
        body.setLink("https://github.com/nowhale26/loganalyzer");
        body.setTags(List.of("work","hobby"));
        body.setFilters(List.of("comment:dummy","job:work"));
        linksService.addLink(125L, body);
        body.setLink("https://github.com/nowhale26/loganalyzer");
        body.setTags(List.of("football","hockey"));
        body.setFilters(List.of("user:dummy","job:work"));
        linksService.addLink(125L,body);
        Set<Link> links = repository.getRepository().get(125L);
        for(var link:links){
            assertThat(List.of("football","hockey"))
                .isEqualTo(link.getTags());
            assertThat(List.of("user:dummy","job:work"))
                .isEqualTo(link.getFilters());
        }

    }




}
