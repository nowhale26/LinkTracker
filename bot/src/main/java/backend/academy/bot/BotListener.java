package backend.academy.bot;

import backend.academy.bot.message.MessageExecutor;
import backend.academy.bot.message.command.Command;
import backend.academy.bot.message.statecommand.StatefulMessageExecutor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BotListener {

    @Value("${app.telegram-token:1234}")
    private String token;

    private final TelegramBot bot;

    private static final String UNKNOWN_COMMAND ="Неизвестная команда";
    private static final String USE_COMMAND = "Используйте команду (/...)";

    private Map<String, MessageExecutor> executors = new HashMap<>();
    private final Map<Long, StatefulMessageExecutor> activeDialogs = new ConcurrentHashMap<>();

    public BotListener(List<MessageExecutor> messageExecutors, TelegramBot bot) {
        for (var messageExecutor : messageExecutors) {
            executors.put(messageExecutor.getExecutorName(), messageExecutor);
        }
        this.bot = bot;
    }

    @PostConstruct
    public void init() {

        // Register for updates
        bot.setUpdatesListener(updates -> {
            for (var update : updates) {
                String text = update.message().text();
                Long chatId = update.message().chat().id();
                if (text.startsWith("/") && activeDialogs.isEmpty()) {
                    String command = text.split("\\s+")[0];
                    if(executors.containsKey(command)){
                        MessageExecutor executor = executors.get(command);
                        executor.execute(update, bot);
                        if (executor instanceof StatefulMessageExecutor statefulExecutor) {
                            if (statefulExecutor.isChatInDialog(chatId)) {
                                activeDialogs.put(chatId, statefulExecutor);
                            }
                        }
                    } else{
                        bot.execute(new SendMessage(chatId, UNKNOWN_COMMAND));
                    }
                } else{
                    StatefulMessageExecutor activeExecutor = activeDialogs.get(chatId);
                    if (activeExecutor != null) {
                        activeExecutor.handleUpdate(update);
                        // Если диалог завершился, убираем из activeDialogs
                        if (!activeExecutor.isChatInDialog(chatId)) {
                            activeDialogs.remove(chatId);
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, USE_COMMAND));
                    }
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
