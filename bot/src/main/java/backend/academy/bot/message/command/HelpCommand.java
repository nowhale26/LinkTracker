package backend.academy.bot.message.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class HelpCommand extends Command {
    private final List<Command> commands;

    public HelpCommand(List<Command> commands) {
        super("/help", "Выводит информацию по каждой комманде");
        this.commands = commands;
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        StringBuilder message = new StringBuilder();
        for (var command : commands) {
            if (command.getName() == null) {
                continue;
            }
            message.append(command.getName()).append(": ").append(command.getDescription()).append("\n");
        }
        bot.execute(new SendMessage(chatId, message.toString()));
    }
}
