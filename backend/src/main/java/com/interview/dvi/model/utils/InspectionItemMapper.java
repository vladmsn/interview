package com.interview.dvi.model.utils;

import com.interview.dvi.model.dto.CreateItemRequest;
import lombok.experimental.UtilityClass;

import com.interview.dvi.model.dto.ItemResponse;
import com.interview.dvi.model.entities.InspectionItem;

@UtilityClass
public class InspectionItemMapper {

    public static InspectionItem toEntity(CreateItemRequest request) {
        var entity = new InspectionItem();
        entity.setNote(request.note());
        entity.setCategory(request.category());
        entity.setSeverity(request.severity());
        return entity;
    }

    public static ItemResponse toResponse(InspectionItem item) {
        return new ItemResponse(
                item.getId(),
                item.getNote(),
                item.getCategory(),
                item.getSeverity()
        );
    }
}
