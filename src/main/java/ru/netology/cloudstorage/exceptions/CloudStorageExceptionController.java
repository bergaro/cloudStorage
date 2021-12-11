package ru.netology.cloudstorage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CloudStorageExceptionController {


    @ExceptionHandler(UserNotExistException.class)
    public ResponseEntity<Map<String, Object>> UserNotExistErrorHandler(UserNotExistException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("id", 0);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CredentialException.class)
    public ResponseEntity<? extends Map<String, Object>> CredentialErrorHandler(CredentialException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("id", 0);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<? extends Map<String, Object>> SaveFileException(FileException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("id", 0);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
