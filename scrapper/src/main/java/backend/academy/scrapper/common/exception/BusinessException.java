package backend.academy.scrapper.common.exception;

public class BusinessException extends ScrapperException{


    public BusinessException(String message, String code, String name) {
        super(message, code, name);
    }

    public BusinessException(String message, String code, String name, Throwable cause) {
        super(message, code, name, cause);
    }
}
