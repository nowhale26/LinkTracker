package backend.academy.bot;

import static backend.academy.bot.BotListener.UNKNOWN_COMMAND;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import backend.academy.bot.common.exception.BotException;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.utility.BotUtils;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BotListenerTest extends BaseTest {
    private final BotListener botListener;
    private final String updateJson =
            """
            {
                "message": {
                    "text": "/track1 ",
                    "chat": {
                        "id": "123"
                    }
                }
            }
        """;

    @Autowired
    public BotListenerTest(BotListener botListener) {
        this.botListener = botListener;
    }

    @Test
    public void handleUpdatesTest() {
        Update unkownCommmand = BotUtils.parseUpdate(updateJson);
        List<Update> updates = List.of(unkownCommmand);
        try {
            botListener.handleUpdates(updates);
            failBecauseExceptionWasNotThrown(BotException.class);
        } catch (BotException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo(UNKNOWN_COMMAND);
        }
    }
}
