package backend.academy.bot.common.handler;

import backend.academy.bot.scrapperservice.client.model.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(basePackages = {"backend.academy.bot.scrapperservice.controller"})
@Slf4j
public class UpdatesExceptionHandler {
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
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(400));
    }
}

