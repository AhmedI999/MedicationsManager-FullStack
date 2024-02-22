package com.simplesolutions.medicinesmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class OwnershipException extends RuntimeException{
    public OwnershipException(String message) {
        super(message);
    }
}
