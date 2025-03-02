package backend.academy.bot.message.command;

import backend.academy.bot.message.MessageExecutor;
import backend.academy.bot.scrapperservice.client.ScrapperClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public abstract class Command implements MessageExecutor {
    @Autowired
    protected ScrapperClient client;

    private String name;
    private String description;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public abstract void execute(Update update, TelegramBot bot);

    @Override
    public String getExecutorName() {
        return name;
    }
}
