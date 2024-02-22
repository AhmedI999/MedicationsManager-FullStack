package com.simplesolutions.medicinesmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ConfirmationRequestExpiredException extends RuntimeException{
    public ConfirmationRequestExpiredException(String message) {
        super(message);
    }
}
