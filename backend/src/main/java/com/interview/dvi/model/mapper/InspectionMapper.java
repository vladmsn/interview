package com.interview.dvi.model.mapper;

import lombok.experimental.UtilityClass;

import com.interview.dvi.model.dto.CreateInspectionRequest;
import com.interview.dvi.model.dto.InspectionResponse;
import com.interview.dvi.model.entities.Inspection;

@UtilityClass
public final class InspectionMapper {

    public static Inspection toEntity(CreateInspectionRequest request) {
        var entity = new Inspection();
        entity.setVin(request.vin());
        entity.setNote(request.note());
        entity.setRecommendation(request.recommendation());
        entity.setEstimatedCost(request.estimatedCost());
        return entity;
    }

    public static InspectionResponse toResponse(Inspection inspection) {
        return new InspectionResponse(
                inspection.getId(),
                inspection.getVin(),
                inspection.getNote(),
                inspection.getStatus(),
                inspection.getRecommendation(),
                inspection.getEstimatedCost()
        );
    }
}
