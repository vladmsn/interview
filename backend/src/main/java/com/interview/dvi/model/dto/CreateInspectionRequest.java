package com.interview.dvi.model.dto;

import java.math.BigDecimal;

public record CreateInspectionRequest (
        String vin,
        String note,
        String recommendation,
        BigDecimal estimatedCost
) {}
