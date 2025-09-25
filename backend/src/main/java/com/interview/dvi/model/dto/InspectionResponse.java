package com.interview.dvi.model.dto;

import java.math.BigDecimal;

import com.interview.dvi.model.enums.Status;

public record InspectionResponse (
        Integer id,
        String vin,
        String note,
        Status status,
        String recommendation,
        BigDecimal estimatedCost
) {}
