package backend.academy.bot.scrapperservice.controller;

import backend.academy.bot.scrapperservice.controller.model.LinkUpdate;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;

@Service
public class UpdatesService {

    private final TelegramBot bot;

    public UpdatesService(TelegramBot bot){
        this.bot = bot;
    }

    public void sendUpdate(LinkUpdate update){
        for(var tgChatId : update.getTgChatIds()){
            String message = "Ссылка: "+update.getUrl()+" Сообщение: " + update.getDescription();
            bot.execute(new SendMessage(tgChatId, message));
        }
    }
}
