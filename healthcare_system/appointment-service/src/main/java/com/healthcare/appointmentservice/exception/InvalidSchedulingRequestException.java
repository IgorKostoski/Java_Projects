package com.healthcare.appointmentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSchedulingRequestException extends RuntimeException {

    public InvalidSchedulingRequestException(String message) {
        super(message);
    }

    public InvalidSchedulingRequestException(String message, Throwable cause) {
        super(message, cause);
    }


}
