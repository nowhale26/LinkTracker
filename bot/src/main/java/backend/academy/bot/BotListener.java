package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotListener {

    @Value("${TELEGRAM_TOKEN}")
    private String token;

    @PostConstruct
    public void init(){
        TelegramBot bot = new TelegramBot(token);

// Register for updates
        bot.setUpdatesListener(updates -> {
            for(var update : updates){
                long chatId = update.message().chat().id();
                bot.execute(new SendMessage(chatId, update.message().text()));
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                // got bad response from telegram
                e.response().errorCode();
                e.response().description();
            } else {
                // probably network error
                e.printStackTrace();
            }
        });

    }
}
