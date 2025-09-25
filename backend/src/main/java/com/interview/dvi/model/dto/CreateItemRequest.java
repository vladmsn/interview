package com.interview.dvi.model.dto;

import com.interview.dvi.model.enums.Severity;

public record CreateItemRequest (
        String note,
        String category,
        Severity severity
) {}
