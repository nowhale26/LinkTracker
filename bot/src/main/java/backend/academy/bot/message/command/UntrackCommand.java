package backend.academy.bot.message.command;

import backend.academy.bot.message.MessageExecutor;
import backend.academy.bot.service.model.RemoveLinkRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand extends Command implements MessageExecutor {

    public UntrackCommand(){
        super("/untrack","Удаляет ссылку для отслеживания");
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        RemoveLinkRequest request = new RemoveLinkRequest();
        String url = update.message().text().split("\\s+")[1];
        request.link(url);
        service.removeLink(chatId, request);
        bot.execute(new SendMessage(chatId, "Ссылка удалена"));
    }

}
