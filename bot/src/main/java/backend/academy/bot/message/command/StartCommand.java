package backend.academy.bot.message.command;

import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.message.MessageExecutor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class StartCommand extends Command implements MessageExecutor {

    public StartCommand() {
        super("/start", "Регистарция чата");
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        try {
            client.registerChat(chatId);
        } catch (ScrapperClientException e) {
            bot.execute(new SendMessage(chatId, e.getMessage()));
        }
        bot.execute(new SendMessage(chatId, "Чат успешно зарегистрирован"));
    }
}
