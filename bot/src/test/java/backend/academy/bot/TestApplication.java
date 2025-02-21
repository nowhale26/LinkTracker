package backend.academy.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Profile;

@Profile("test")
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.from(BotApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
