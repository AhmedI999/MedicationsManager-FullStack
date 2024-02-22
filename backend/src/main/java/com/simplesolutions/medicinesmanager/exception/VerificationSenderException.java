package com.simplesolutions.medicinesmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class VerificationSenderException extends RuntimeException{
    public VerificationSenderException(String message) {
        super(message);
    }
}
