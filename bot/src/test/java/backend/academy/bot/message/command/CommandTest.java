package backend.academy.bot.message.command;

import backend.academy.bot.BaseTest;
import backend.academy.bot.common.exception.BotException;
import backend.academy.bot.common.exception.ScrapperClientException;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.utility.BotUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class CommandTest extends BaseTest {

    private final TrackCommand trackCommand;
    private final UntrackCommand untrackCommand;
    private final ListCommand listCommand;

    @Autowired
    public CommandTest(TrackCommand trackCommand, UntrackCommand untrackCommand, ListCommand listCommand) {
        this.trackCommand = trackCommand;
        this.untrackCommand = untrackCommand;
        this.listCommand = listCommand;
    }

    @Test
    public void trackCommandTest(){
        try{
            trackCommand.completeTracking(1L,null);
            failBecauseExceptionWasNotThrown(BotException.class);
        } catch (BotException e){
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка на добавление");
        }
    }

    @Test
    public void unTrackCommandTest(){
        Update command = BotUtils.parseUpdate("{\n" +
            "    \"message\": {\n" +
            "        \"text\": \"/track 123 \",\n" +
            "        \"chat\": {\n" +
            "            \"id\": \"123\"\n" +
            "        }\n" +
            "    }\n" +
            "}");
        try{
            untrackCommand.execute(command,null);
            failBecauseExceptionWasNotThrown(BotException.class);
        } catch (BotException e){
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка на удаление");
        }
    }

    @Test
    public void listCommandTest(){
        Update command = BotUtils.parseUpdate("{\n" +
            "    \"message\": {\n" +
            "        \"text\": \"/track 123 \",\n" +
            "        \"chat\": {\n" +
            "            \"id\": \"123\"\n" +
            "        }\n" +
            "    }\n" +
            "}");
        try{
            listCommand.execute(command,null);
            failBecauseExceptionWasNotThrown(BotException.class);
        } catch (BotException e){
            assertThat(e.getMessage()).isEqualTo("Некорректная ссылка на получение");
        }
    }
}
