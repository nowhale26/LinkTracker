package backend.academy.bot.message.command;


import backend.academy.bot.message.MessageExecutor;
import backend.academy.bot.service.ScrapperService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class Command implements MessageExecutor {
    @Autowired
    protected ScrapperService service;

    private String name;
    private String description;

    public Command(String name, String description){
        this.name = name;
        this.description = description;
    }

    @Override
    public void execute(Update update, TelegramBot bot) {

    }

    @Override
    public String getExecutorName() {
        return name;
    }
}
