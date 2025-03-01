package backend.academy.bot;

import backend.academy.bot.common.exception.BotException;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.utility.BotUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import static backend.academy.bot.BotListener.UNKNOWN_COMMAND;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class BotListenerTest extends BaseTest {
    private final BotListener botListener;

    @Autowired
    public BotListenerTest(BotListener botListener) {
        this.botListener = botListener;
    }


    @Test
    public void handleUpdatesTest() {
        Update unkownCommmand = BotUtils.parseUpdate("{\n" +
            "    \"message\": {\n" +
            "        \"text\": \"/track1 \",\n" +
            "        \"chat\": {\n" +
            "            \"id\": \"123\"\n" +
            "        }\n" +
            "    }\n" +
            "}");
        List<Update> updates = List.of(unkownCommmand);
        try {
            botListener.handleUpdates(updates);
            failBecauseExceptionWasNotThrown(BotException.class);
        } catch (BotException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(UNKNOWN_COMMAND);
        }
    }
}
