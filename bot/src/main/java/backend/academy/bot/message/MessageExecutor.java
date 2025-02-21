package backend.academy.bot.message;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;

@Component
public interface MessageExecutor {
    void execute(Update update, TelegramBot bot);
    String getExecutorName();

}
