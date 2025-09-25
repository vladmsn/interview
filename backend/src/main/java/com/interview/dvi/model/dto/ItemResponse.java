package com.interview.dvi.model.dto;

import com.interview.dvi.model.enums.Severity;

public record ItemResponse(
        Integer id,
        String note,
        String category,
        Severity severity
) {}
