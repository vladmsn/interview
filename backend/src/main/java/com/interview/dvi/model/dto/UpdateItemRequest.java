package com.interview.dvi.model.dto;

import com.interview.dvi.model.enums.Severity;

public record UpdateItemRequest (
        String note,
        String category,
        Severity severity
) {}
