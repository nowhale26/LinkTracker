package backend.academy.scrapper.common.handler;

import backend.academy.scrapper.common.exception.BusinessException;
import backend.academy.scrapper.common.exception.ScrapperException;
import backend.academy.scrapper.links.model.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(basePackages = {"backend.academy.scrapper"})
@Slf4j
public class ScrapperExceptionHandler {
    @ExceptionHandler({BusinessException.class, ScrapperException.class})
    public ResponseEntity<ApiErrorResponse> handleScrapperException(final ScrapperException exception) {
        log.info(exception.getMessage(), exception);
        var response = new ApiErrorResponse();
        response.setExceptionMessage(exception.getMessage());
        response.setCode(exception.getCode());
        response.setExceptionName(exception.getName());
        List<String> stackTrace = new ArrayList<>();
        for (var trace : exception.getStackTrace()) {
            stackTrace.add(trace.toString());
        }
        response.setStacktrace(stackTrace);
        if (exception instanceof BusinessException) {
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(400));
        } else {
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(500));
        }

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(final Exception exception) {
        log.info(exception.getMessage(), exception);
        var response = new ApiErrorResponse();
        response.setExceptionMessage(exception.getMessage());
        response.setCode("1");
        response.setExceptionName("System exception");
        List<String> stackTrace = new ArrayList<>();
        for (var trace : exception.getStackTrace()) {
            stackTrace.add(trace.toString());
        }
        response.setStacktrace(stackTrace);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(500));
    }
}
