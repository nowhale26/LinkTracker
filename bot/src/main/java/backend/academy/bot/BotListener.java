package backend.academy.bot;

import backend.academy.bot.message.MessageExecutor;
import backend.academy.bot.message.command.Command;
import backend.academy.bot.message.statecommand.StatefulMessageExecutor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BotListener {

    private final TelegramBot bot;

    public static final String UNKNOWN_COMMAND = "Неизвестная команда";
    private static final String USE_COMMAND = "Используйте команду (/...)";

    private final BotCommand[] botCommands;

    private final Map<String, MessageExecutor> executors = new HashMap<>();
    private final Map<Long, StatefulMessageExecutor> activeDialogs = new ConcurrentHashMap<>();

    public BotListener(List<MessageExecutor> messageExecutors, List<Command> commands, TelegramBot bot) {
        for (var messageExecutor : messageExecutors) {
            executors.put(messageExecutor.getExecutorName(), messageExecutor);
        }
        botCommands = new BotCommand[commands.size()];
        for (int i = 0; i < botCommands.length; i++) {
            String name = commands.get(i).getName();
            String description = commands.get(i).getDescription();
            botCommands[i] = new BotCommand(name, description);
        }
        this.bot = bot;
    }

    @PostConstruct
    public void init() {

        bot.execute(new SetMyCommands(botCommands));

        // Register for updates
        bot.setUpdatesListener(
                updates -> {
                    return handleUpdates(updates);
                },
                error -> {
                    if (error.response() != null) {
                        // got bad response from telegram
                        log.error("Error code: {}", error.response().errorCode());
                        log.error("Error description: {}", error.response().description());
                    } else {
                        // probably network error
                        error.printStackTrace();
                    }
                });
    }

    protected int handleUpdates(List<Update> updates) {
        for (var update : updates) {
            String text = update.message().text();
            Long chatId = update.message().chat().id();
            if (text.startsWith("/") && !activeDialogs.containsKey(chatId)) {
                String command = text.split("\\s+")[0];
                if (executors.containsKey(command)) {
                    MessageExecutor executor = executors.get(command);
                    executor.execute(update, bot);
                    if (executor instanceof StatefulMessageExecutor statefulExecutor
                            && statefulExecutor.isChatInDialog(chatId)) {
                        activeDialogs.put(chatId, statefulExecutor);
                    }
                } else {
                    bot.execute(new SendMessage(chatId, UNKNOWN_COMMAND));
                }
            } else {
                StatefulMessageExecutor activeExecutor = activeDialogs.get(chatId);
                if (activeExecutor != null) {
                    activeExecutor.handleUpdate(update);
                    if (!activeExecutor.isChatInDialog(chatId)) {
                        activeDialogs.remove(chatId);
                    }
                } else {
                    bot.execute(new SendMessage(chatId, USE_COMMAND));
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
