package backend.academy.bot.message.command;

import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.message.MessageExecutor;
import backend.academy.bot.scrapperservice.client.model.RemoveLinkRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends Command implements MessageExecutor {

    public UntrackCommand() {
        super("/untrack", "Удаляет ссылку для отслеживания");
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        RemoveLinkRequest request = new RemoveLinkRequest();
        String[] updateMessage = update.message().text().split("\\s+");
        String url;
        if (updateMessage.length == 2) {
            url = updateMessage[1];
        } else {
            bot.execute(new SendMessage(chatId, "После команды нужно написать ссылку на ресурс"));
            return;
        }
        request.setLink(url);
        try {
            client.removeLink(chatId, request);
        } catch (ScrapperClientException e) {
            String message = e.getMessage();
            bot.execute(new SendMessage(chatId, message));
            return;
        }
        bot.execute(new SendMessage(chatId, "Ссылка удалена"));
    }
}
