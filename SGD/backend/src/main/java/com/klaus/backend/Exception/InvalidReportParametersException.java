package com.klaus.backend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid report parameters")
public class InvalidReportParametersException extends RuntimeException {

    public InvalidReportParametersException(String message) {
        super(message);
    }

    public InvalidReportParametersException(String message, Throwable cause) {
        super(message, cause);
    }

}
