package com.interview.dvi.model.dto;

public record ReviewRequest (
        Integer inspectionId,
        String decision,
        long expiryEpochSeconds
) {}
