package backend.academy.scrapper.links;

import backend.academy.scrapper.BaseTest;
import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.links.model.AddLinkRequest;
import backend.academy.scrapper.links.model.LinkResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;


public class LinksServiceTest extends BaseTest {

    private final LinksService linksService;

    @Autowired
    public LinksServiceTest(LinksService linksService) {
        this.linksService = linksService;
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


}
