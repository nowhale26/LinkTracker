package backend.academy.scrapper;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.OrmLinkRepository;
import backend.academy.scrapper.repository.SqlLinkRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RepositoryConfigurationTest extends BaseTest {
    private final String accessType;
    private final LinkRepository repository;

    @Autowired
    public RepositoryConfigurationTest(ScrapperConfig config, LinkRepository repository) {
        this.accessType = config.accessType();
        this.repository = repository;
    }

    @Test
    public void repositoryTypeTest() {
        if (accessType.equals("ORM")) {
            boolean correctDbType = repository instanceof OrmLinkRepository;
            assertThat(correctDbType).isTrue();
        } else if (accessType.equals("SQL")) {
            boolean correctDbType = repository instanceof SqlLinkRepository;
            assertThat(correctDbType).isTrue();
        }
    }
}
