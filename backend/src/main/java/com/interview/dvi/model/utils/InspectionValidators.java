package com.interview.dvi.model.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.interview.dvi.model.dto.CreateInspectionRequest;
import com.interview.dvi.model.dto.CreateItemRequest;
import com.interview.dvi.model.dto.UpdateInspectionRequest;
import com.interview.dvi.model.dto.UpdateItemRequest;
import com.interview.dvi.model.dto.ValidationError;
import com.interview.dvi.model.exceptions.ValidationException;

@UtilityClass
public class InspectionValidators {

    private static final Pattern VIN_PATTERN = Pattern.compile("^[A-HJ-NPR-Z0-9]{17}$");
    private static final int NOTE_MAX_LENGTH = 255;
    private static final int CATEGORY_MAX_LENGTH = 64;

    public static void validateCreateInspection(CreateInspectionRequest r) {
        var errors = new ArrayList<ValidationError>();
        if (isBlank(r.vin()) || !VIN_PATTERN.matcher(r.vin()).matches()) {
            errors.add(err("vin", "VIN_INVALID",
                    "VIN must be 11–17 chars, A–Z/0–9, excluding I,O,Q."));
        }
        if (tooLong(r.note(), NOTE_MAX_LENGTH)) {
            errors.add(err("note", "TEXT_TOO_LONG",
                    "Note max length is " + NOTE_MAX_LENGTH + "."));
        }
        if (tooLong(r.recommendation(), NOTE_MAX_LENGTH)) {
            errors.add(err("recommendation", "TEXT_TOO_LONG",
                    "Recommendation max length is " + NOTE_MAX_LENGTH + "."));
        }
        if (r.estimatedCost() != null) {
            if (r.estimatedCost().compareTo(BigDecimal.ZERO) < 0) {
                errors.add(err("estimatedCost", "NEGATIVE", "Estimated cost must be >= 0."));
            }
            if (r.estimatedCost().scale() > 2) {
                errors.add(err("estimatedCost", "SCALE_TOO_PRECISE", "Estimated cost supports up to 2 decimals."));
            }
        }

        throwIf(errors);
    }


    public static void validateUpdateInspection(UpdateInspectionRequest r) {
        var errors = new ArrayList<ValidationError>();
        if (tooLong(r.note(), NOTE_MAX_LENGTH)) {
            errors.add(err("note", "TEXT_TOO_LONG",
                    "Note max length is " + NOTE_MAX_LENGTH + "."));
        }
        if (tooLong(r.recommendation(), NOTE_MAX_LENGTH)) {
            errors.add(err("recommendation", "TEXT_TOO_LONG",
                    "Recommendation max length is " + NOTE_MAX_LENGTH + "."));
        }
        if (r.estimatedCost() != null) {
            if (r.estimatedCost().compareTo(BigDecimal.ZERO) < 0) {
                errors.add(err("estimatedCost", "NEGATIVE", "Estimated cost must be >= 0."));
            }
            if (r.estimatedCost().scale() > 2) {
                errors.add(err("estimatedCost", "SCALE_TOO_PRECISE",
                        "Estimated cost supports up to 2 decimals."));
            }
        }

        throwIf(errors);
    }


    public static void validateCreateItem(CreateItemRequest r) {
        var errors = new ArrayList<ValidationError>();
        if (isBlank(r.category())) {
            errors.add(err("category", "REQUIRED", "Category is required."));
        } else if (r.category().length() > CATEGORY_MAX_LENGTH) {
            errors.add(err("category", "TEXT_TOO_LONG",
                    "Category max length is " + CATEGORY_MAX_LENGTH + "."));
        }
        if (r.severity() == null) {
            errors.add(err("severity", "REQUIRED", "Severity is required."));
        }
        if (tooLong(r.note(), NOTE_MAX_LENGTH)) {
            errors.add(err("note", "TEXT_TOO_LONG",
                    "Note max length is " + NOTE_MAX_LENGTH + "."));
        }

        throwIf(errors);
    }


    public static void validateUpdateItem(UpdateItemRequest r) {
        var errors = new ArrayList<ValidationError>();
        if (r.category() == null) {
            errors.add(err("category", "REQUIRED", "Category is required."));
        } else {
            if (isBlank(r.category())) {
                errors.add(err("category", "BLANK", "Category cannot be blank."));
            } else if (r.category().length() > CATEGORY_MAX_LENGTH) {
                errors.add(err("category", "TEXT_TOO_LONG",
                        "Category max length is " + CATEGORY_MAX_LENGTH + "."));
            }
        }
        if (r.severity() == null) {
            errors.add(err("severity", "REQUIRED", "Severity is required."));
        }
        if (tooLong(r.note(), NOTE_MAX_LENGTH)) {
            errors.add(err("note", "TEXT_TOO_LONG",
                    "Note max length is " + NOTE_MAX_LENGTH + "."));
        }

        throwIf(errors);
    }


    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static boolean tooLong(String s, int max) {
        return s != null && s.length() > max;
    }

    private static ValidationError err(String field, String code, String message) {
        return new ValidationError(field, code, message);
    }

    private static void throwIf(List<ValidationError> errors) {
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
