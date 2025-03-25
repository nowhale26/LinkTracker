package backend.academy.bot.message.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import backend.academy.bot.BaseTest;
import backend.academy.bot.common.exception.BotException;
import backend.academy.bot.scrapperservice.client.model.LinkResponse;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.utility.BotUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CommandTest extends BaseTest {

    private final TrackCommand trackCommand;
    private final UntrackCommand untrackCommand;
    private final ListCommand listCommand;
    private final String updateJson =
            """
            {
                "message": {
                    "text": "/track 123 ",
                    "chat": {
                        "id": "123"
                    }
                }
            }
        """;

    private final Update command = BotUtils.parseUpdate(updateJson);

    @Autowired
    public CommandTest(TrackCommand trackCommand, UntrackCommand untrackCommand, ListCommand listCommand) {
        this.trackCommand = trackCommand;
        this.untrackCommand = untrackCommand;
        this.listCommand = listCommand;
    }

    @Test
    public void trackCommandTest() {
        BotException exception = assertThrows(BotException.class, () -> trackCommand.completeTracking(1L, null));
        assertThat(exception.getMessage()).isEqualTo("Некорректная ссылка на добавление");
    }

    @Test
    public void unTrackCommandTest() {
        BotException exception = assertThrows(BotException.class, () -> untrackCommand.execute(command, null));
        assertThat(exception.getMessage()).isEqualTo("Некорректная ссылка на удаление");
    }

    @Test
    public void listCommandTest() {
        BotException exception = assertThrows(BotException.class, () -> listCommand.execute(command, null));
        assertThat(exception.getMessage()).isEqualTo("Некорректная ссылка на получение");
    }

    @Test
    public void listFormattingTest() {
        List<LinkResponse> links =
                List.of(new LinkResponse().url("abc"), new LinkResponse().url("def"), new LinkResponse().url("xyz"));
        String message = listCommand.createListMessage(links);
        String expectedMessage = "abc\n" + "def\n" + "xyz\n";
        assertThat(message).isEqualTo(expectedMessage);
    }
}
