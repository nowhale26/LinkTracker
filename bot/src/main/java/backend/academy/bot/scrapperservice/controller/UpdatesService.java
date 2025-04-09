package backend.academy.bot.scrapperservice.controller;

import backend.academy.bot.scrapperservice.controller.model.LinkUpdate;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;

@Service
public class UpdatesService {

    private final TelegramBot bot;

    public UpdatesService(TelegramBot bot) {
        this.bot = bot;
    }

    public void sendUpdate(LinkUpdate update) {
        String message = "Обновление: \n\n" + update.getDescription();
        bot.execute(new SendMessage(update.getTgChatId(), message));
    }
}
