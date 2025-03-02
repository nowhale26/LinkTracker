package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {
    private String botToken;

    public TelegramBotConfig(BotConfig botConfig) {
        botToken = botConfig.telegramToken();
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }
}
