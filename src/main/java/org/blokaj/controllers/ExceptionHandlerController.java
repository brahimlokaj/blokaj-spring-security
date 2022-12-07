package org.blokaj.controllers;

import org.blokaj.exceptions.RefreshTokenBadRequest;
import org.blokaj.models.responses.FieldError;
import org.blokaj.models.responses.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerController {


    /**
     * Returns the list of fields when the fields have a validation error
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<List<FieldError>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldsWithErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());


        Response<List<FieldError>> response = new Response<>();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setData(fieldsWithErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(response);
    }

    @ExceptionHandler(RefreshTokenBadRequest.class)
    public ResponseEntity<Response<String>> handleRefreshTokenBadRequestException(RefreshTokenBadRequest ex) {

        Response<String> response = new Response<>();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setData(ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(response);
    }
}
