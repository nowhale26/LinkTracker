package backend.academy.bot.message.command;

import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.scrapperservice.client.model.EnableTagRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class TagCommand extends Command {

    private final String BAD_MESSAGE = "После команды нужно написать 1 (да) или 0 (нет)";

    public TagCommand() {
        super("/tag", "Формирование сообщений об обновлениях ссылок по тэгам(1 - да, 0 - нет");
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        String[] updateMessage = update.message().text().split("\\s+");
        int answer;
        if(updateMessage.length == 2) {
            try{
                answer = Integer.parseInt(updateMessage[1]);
            } catch (NumberFormatException e) {
                bot.execute(new SendMessage(chatId, BAD_MESSAGE));
                return;
            }
            if(answer!=1 && answer!=0) {
                bot.execute(new SendMessage(chatId, BAD_MESSAGE));
            } else {
                boolean enableTagInUpdates = answer == 1;
                try{
                    client.enableTagInUpdates(chatId, new EnableTagRequest().setEnableTagInUpdates(enableTagInUpdates));
                    String reply = answer==1 ? "включено" : "выключено";
                    bot.execute(new SendMessage(chatId, "Формирование сообщений об обновлениях ссылок по тэгам "+reply));
                } catch (ScrapperClientException e){
                    bot.execute(new SendMessage(chatId, e.getMessage()));
                }
            }
        } else{
            bot.execute(new SendMessage(chatId, BAD_MESSAGE));
        }
    }
}
