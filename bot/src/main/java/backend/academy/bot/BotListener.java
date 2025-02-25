package backend.academy.bot;

import backend.academy.bot.message.MessageExecutor;
import backend.academy.bot.message.command.Command;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotListener {

    @Value("${app.telegram-token:1234}")
    private String token;

    private static final String UNKNOWN_COMMAND ="Неизвестная команда";
    private static final String USE_COMMAND = "Используйте команду (/...)";

    private Map<String, MessageExecutor> executors = new HashMap<>();

    public BotListener(List<MessageExecutor> messageExecutors) {
        for (var messageExecutor : messageExecutors) {
            executors.put(messageExecutor.getExecutorName(), messageExecutor);
        }
    }

    @PostConstruct
    public void init() {
        TelegramBot bot = new TelegramBot(token);

        // Register for updates
        bot.setUpdatesListener(updates -> {
            for (var update : updates) {
                String text = update.message().text();
                Long chatId = update.message().chat().id();
                if (text.startsWith("/")) {
                    String command = text.split("\\s+")[0];
                    if(executors.containsKey(command)){
                        executors.get(command).execute(update, bot);
                    } else{
                        bot.execute(new SendMessage(chatId, UNKNOWN_COMMAND));
                    }
                } else{
                    bot.execute(new SendMessage(chatId, USE_COMMAND));
                }
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
