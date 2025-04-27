package backend.academy.bot.message.command;

import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.scrapperservice.client.model.EnableDigestRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Component;

@Component
public class DigestCommand extends Command {

    private final String BAD_MESSAGE = "После команды нужно написать 0 (выключить) или время в формате HH:mm";

    public DigestCommand() {
        super("/digest", "Формирование дайджеста обновлений (0 - выключить, HH:mm - время отправления дайджеста");
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        String[] updateMessage = update.message().text().split("\\s+");
        if (updateMessage.length == 2) {
            EnableDigestRequest request = new EnableDigestRequest();
            String message;
            if (updateMessage[1].equals("0")) {
                request.setEnableDigest(false);
                try {
                    client.enableDigest(chatId, request);
                    message = "Дайджест успешно выключен";
                } catch (ScrapperClientException e) {
                    message = e.getMessage();
                }
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime time;
                try {
                    time = LocalTime.parse(updateMessage[1], formatter);
                    request.setEnableDigest(true);
                    request.setDigestTime(time);
                    try {
                        client.enableDigest(chatId, request);
                        message = "Дайджест успешно включен на время " + time;
                    } catch (ScrapperClientException e) {
                        message = e.getMessage();
                    }
                } catch (DateTimeParseException e) {
                    message = BAD_MESSAGE;
                }
            }
            sendMessageToBot(chatId, message, bot);
        }
    }
}
