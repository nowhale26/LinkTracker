package backend.academy.bot.message.statecommand;

import backend.academy.bot.message.command.Command;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StatefulCommand<T extends Enum<T>> extends Command implements StatefulMessageExecutor {
    protected final TelegramBot bot;
    protected final Map<Long, T> chatStates = new ConcurrentHashMap<>();
    protected final Map<Long, Object> chatData = new ConcurrentHashMap<>();

    public StatefulCommand(String command, String description, TelegramBot bot) {
        super(command, description);
        this.bot = bot;
    }

    @Override
    public boolean isChatInDialog(Long chatId) {
        return chatStates.containsKey(chatId);
    }

    @Override
    public abstract void execute(Update update, TelegramBot bot);

    @Override
    public abstract void handleUpdate(Update update);

    protected void resetState(Long chatId) {
        chatStates.remove(chatId);
        chatData.remove(chatId);
    }
}
