package com.interview.dvi.utils;

import com.interview.dvi.model.entities.Inspection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestDataUtils {
    public static final String TEST_VIN1 = "1HGCM82633A123456";
    public static final String TEST_VIN2 = "1HGCM82633A654321";

    public static Inspection getTestInspectionDraft(String vin) {
        Inspection inspection = new Inspection();
        inspection.setVin(vin);
        inspection.setNote("Initial Inspection started.");
        inspection.setStatus(com.interview.dvi.model.enums.Status.DRAFT);
        return inspection;
    }
}
