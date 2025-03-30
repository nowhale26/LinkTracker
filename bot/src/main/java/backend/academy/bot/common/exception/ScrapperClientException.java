package backend.academy.bot.common.exception;

public class ScrapperClientException extends BotException {
    public ScrapperClientException(String message, String code, String name, Throwable cause) {
        super(message, code, name, cause);
    }

    public ScrapperClientException(String message, String code, String name) {
        super(message, code, name);
    }
}
