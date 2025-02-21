package backend.academy.bot.message.command;

import backend.academy.bot.message.MessageExecutor;
import backend.academy.bot.service.model.AddLinkRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand extends Command implements MessageExecutor {

    public TrackCommand(){
        super("/track", "Добавляет ссылку для отслеживания");
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        AddLinkRequest request = new AddLinkRequest();
        String url = update.message().text().split("\\s+")[1];
        request.link(url);
        service.addLink(chatId, request);
        bot.execute(new SendMessage(chatId, "Ссылка добавлена"));
    }


}
