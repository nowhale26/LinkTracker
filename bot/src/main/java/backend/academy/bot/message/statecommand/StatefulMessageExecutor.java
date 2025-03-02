package backend.academy.bot.message.statecommand;

import backend.academy.bot.message.MessageExecutor;
import com.pengrad.telegrambot.model.Update;

public interface StatefulMessageExecutor extends MessageExecutor {
    void handleUpdate(Update update);

    boolean isChatInDialog(Long chatId);
}
