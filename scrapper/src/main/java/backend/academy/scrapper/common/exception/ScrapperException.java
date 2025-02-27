package backend.academy.scrapper.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapperException extends RuntimeException {
    private String code;
    private String name;

    public ScrapperException(String message, String code, String name) {
        this(message, code, name, null);
    }

    public ScrapperException(String message, String code, String name, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.name = name;
    }

    public ScrapperException(String message, String code){
        super(message,null);
        this.code = code;
    }


}
