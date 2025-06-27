package com.userenrollment.enrollmentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(UUID id) {
        super("Could not find enrollment with id: " + id);
    }
}