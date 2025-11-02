package com.klaus.backend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s não encontrado(a) com identificador: %s", resourceName, identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
