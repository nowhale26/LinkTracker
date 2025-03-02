package backend.academy.bot;

import static backend.academy.bot.BotListener.UNKNOWN_COMMAND;
import static org.mockito.ArgumentMatchers.any;

import backend.academy.bot.common.exception.BotException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.MessagesResponse;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TelegramBotConfig {

    @Primary
    @Bean
    public TelegramBot telegramBot() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.execute(any(SetMyCommands.class))).thenReturn(null);

        Mockito.when(telegramBot.execute(any(SendMessage.class))).thenAnswer((Answer<MessagesResponse>) invocation -> {
            SendMessage argument = invocation.getArgument(0);
            // System.out.println("------------------------------- " + argument.getParameters());
            String message = (String) argument.getParameters().get("text");
            if (UNKNOWN_COMMAND.equals(message)) {
                throw new BotException(message, "1", message);
            }
            if ("Некорректная ссылка".equals(message)) {
                throw new BotException(message, "400", message);
            }
            return null;
        });
        return telegramBot;
    }
}
