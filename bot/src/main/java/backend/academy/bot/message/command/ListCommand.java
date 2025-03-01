package backend.academy.bot.message.command;

import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.scrapperservice.client.model.ListLinksResponse;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;


@Component
public class ListCommand extends Command {

    public ListCommand() {
        super("/list", "Выводит ссылки, которые отслеживаются");
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        ListLinksResponse response;
        try {
            response = client.getLinks(chatId);
        } catch (ScrapperClientException e) {
            String message = e.getMessage();
            bot.execute(new SendMessage(chatId, message));
            return;
        }
        StringBuilder message = new StringBuilder();
        for (var link : response.getLinks()) {
            message.append(link.getUrl()).append("\n");
        }
        bot.execute(new SendMessage(chatId, message.toString()));
    }

}
