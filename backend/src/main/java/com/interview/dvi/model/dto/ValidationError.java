package com.interview.dvi.model.dto;

public record ValidationError(
        String field,
        String code,
        String message
) {}
