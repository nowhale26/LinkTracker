package backend.academy.bot.message.command;

import backend.academy.bot.common.exception.ScrapperClientException;
import backend.academy.bot.message.statecommand.StatefulCommand;
import backend.academy.bot.scrapperservice.client.model.AddLinkRequest;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
public class TrackCommand extends StatefulCommand<TrackCommand.State> {

    public enum State {
        IDLE,
        WAITING_FOR_LINK,
        WAITING_FOR_TAGS,
        WAITING_FOR_FILTERS
    }

    public TrackCommand(TelegramBot bot) {
        super("/track", "Добавляет ссылку для отслеживания", bot);
    }

    @Override
    public void execute(Update update, TelegramBot bot) {
        Long chatId = update.message().chat().id();
        String[] parts = update.message().text().split("\\s+", 2);

        chatStates.put(chatId, State.WAITING_FOR_LINK);
        chatData.put(chatId, new AddLinkRequest());

        if (parts.length == 2) {
            handleLinkInput(chatId, parts[1]);
        } else {
            bot.execute(new SendMessage(chatId, "После команды нужно написать ссылку на ресурс"));
        }
    }

    @Override
    public void handleUpdate(Update update) {
        Long chatId = update.message().chat().id();
        String text = update.message().text();
        State state = (State) chatStates.get(chatId);
        AddLinkRequest request = (AddLinkRequest) chatData.get(chatId);

        boolean correctInput = !text.trim().isEmpty() && !text.equals("1");
        switch (state) {
            case WAITING_FOR_LINK:
                handleLinkInput(chatId, text);
                break;

            case WAITING_FOR_TAGS:
                if (correctInput) {
                    List<String> tags = Arrays.asList(text.split("\\s+"));
                    request.tags(tags);
                }
                moveToFilters(chatId);
                break;

            case WAITING_FOR_FILTERS:
                if (correctInput) {
                    List<String> filters = parseFilters(text);
                    request.filters(filters);
                }
                completeTracking(chatId, request);
                break;

            default:
                break;
        }
    }

    private void handleLinkInput(Long chatId, String url) {
        AddLinkRequest request = (AddLinkRequest) chatData.get(chatId);
        request.link(url);
        chatStates.put(chatId, State.WAITING_FOR_TAGS);
        bot.execute(new SendMessage(chatId, "Введите теги (опционально, введите 1 для пропуска)"));
    }

    private void moveToFilters(Long chatId) {
        chatStates.put(chatId, State.WAITING_FOR_FILTERS);
        bot.execute(new SendMessage(chatId, "Настройте фильтры (опционально, введите 1 для завершения)"));
    }

    protected void completeTracking(Long chatId, AddLinkRequest request) {
        try {
            client.addLink(chatId, request);
            bot.execute(new SendMessage(chatId, "Ссылка успешно добавлена"));
        } catch (ScrapperClientException e) {
            String message = e.getMessage();
            bot.execute(new SendMessage(chatId, message));
        } finally {
            resetState(chatId);
        }
    }

    private List<String> parseFilters(String text) {
        return Arrays.asList(text.split("\\s+"));
    }

}
