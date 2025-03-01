package backend.academy.bot;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import({TestcontainersConfiguration.class})
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {
}
