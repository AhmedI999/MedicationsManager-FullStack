package com.simplesolutions.medicinesmanager.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RegistrationConstraintsException extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        responseBody.put("path", path);
        responseBody.put("timestamp", new Date());
        responseBody.put("status", status.value());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<Map<String, String>> invalidFields = fieldErrors.stream()
                .map(fieldError -> {
                    Map<String, String> fieldErrorMap = new LinkedHashMap<>();
                    fieldErrorMap.put("field", fieldError.getField());
                    fieldErrorMap.put("error", fieldError.getDefaultMessage());
                    return fieldErrorMap;
                })
                .collect(Collectors.toList());

        responseBody.put("invalidFields", invalidFields);

        return new ResponseEntity<>(responseBody, headers, status);
    }
}
