package es.codeurjc.handler;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String FAILURE = "FAILURE";
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        
        return ResponseEntity
                        .status(ex.getStatusCode())
                        .body(Map.of(
                                "status",FAILURE,
                                "statusCode", ex.getStatusCode().value(),
                                "error", ex.getStatusCode().toString(),
                                "message", ex.getReason()
                        ));

    }
}

