package com.interview.dvi.model.exceptions;

import java.util.List;

import com.interview.dvi.model.dto.ValidationError;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = List.copyOf(errors);
    }
}
