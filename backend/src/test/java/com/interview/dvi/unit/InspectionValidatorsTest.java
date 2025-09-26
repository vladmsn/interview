package com.interview.dvi.unit;

import com.interview.dvi.model.dto.CreateInspectionRequest;
import com.interview.dvi.model.dto.CreateItemRequest;
import com.interview.dvi.model.dto.UpdateItemRequest;
import com.interview.dvi.model.enums.Severity;
import com.interview.dvi.model.exceptions.ValidationException;
import com.interview.dvi.model.utils.InspectionValidators;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InspectionValidatorsTest {

    @Test
    void createInspection_invalidVin_throws() {
        var req = new CreateInspectionRequest("IOQ123", "n", null, null);
        var ex = assertThrows(ValidationException.class,
                () -> InspectionValidators.validateCreateInspection(req));
        assertTrue(hasCode(ex, "VIN_INVALID"));
    }

    @Test
    void createInspection_negativeCost_throws() {
        var req = new CreateInspectionRequest("1HGCM82633A004352", "n", null, new BigDecimal("-1.00"));
        var ex = assertThrows(ValidationException.class,
                () -> InspectionValidators.validateCreateInspection(req));
        assertTrue(hasCode(ex, "NEGATIVE"));
    }

    @Test
    void createInspection_tooPreciseCost_throws() {
        var req = new CreateInspectionRequest("1HGCM82633A004352", "n", null, new BigDecimal("1.999"));
        var ex = assertThrows(ValidationException.class,
                () -> InspectionValidators.validateCreateInspection(req));
        assertTrue(hasCode(ex, "SCALE_TOO_PRECISE"));
    }

    @Test
    void createItem_missingSeverity_throws() {
        var req = new CreateItemRequest("Brakes", "pads", null);
        var ex = assertThrows(ValidationException.class,
                () -> InspectionValidators.validateCreateItem(req));
        assertTrue(ex.getErrors().stream().anyMatch(e -> e.field().equals("severity")));
    }

    @Test
    void updateItem_blankCategory_throws() {
        var req = new UpdateItemRequest("", null, Severity.HIGH);
        var ex = assertThrows(ValidationException.class,
                () -> InspectionValidators.validateUpdateItem(req));
        assertTrue(ex.getErrors().stream().anyMatch(e -> e.field().equals("category")));
    }

    private boolean hasCode(ValidationException ex, String code) {
        return ex.getErrors().stream().anyMatch(e -> e.code().equals(code));
    }
}
