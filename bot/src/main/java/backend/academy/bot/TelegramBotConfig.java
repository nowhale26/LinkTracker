package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfig {
    @Value("${app.telegram-token:1234}")
    private String botToken;

    @Bean
    public TelegramBot telegramBot(){
        return new TelegramBot(botToken);
    }
}
