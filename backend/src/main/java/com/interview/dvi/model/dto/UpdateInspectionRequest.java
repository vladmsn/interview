package com.interview.dvi.model.dto;

import java.math.BigDecimal;

public record UpdateInspectionRequest (
    String note,
    String recommendation,
    BigDecimal estimatedCost
) {}
