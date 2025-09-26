package com.interview.dvi.service;

import com.interview.dvi.model.dto.CreateItemRequest;
import com.interview.dvi.model.dto.ItemResponse;
import com.interview.dvi.model.dto.UpdateItemRequest;
import com.interview.dvi.model.entities.Inspection;
import com.interview.dvi.model.entities.InspectionItem;
import com.interview.dvi.model.enums.Status;
import com.interview.dvi.model.exceptions.ConflictException;
import com.interview.dvi.model.exceptions.NotFoundException;
import com.interview.dvi.model.utils.InspectionItemMapper;
import com.interview.dvi.model.utils.InspectionValidators;
import com.interview.dvi.repository.InspectionItemRepository;
import com.interview.dvi.repository.InspectionRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionItemService {
    private final InspectionRepository inspectionRepository;
    private final InspectionItemRepository inspectionItemRepository;

    @Transactional
    public ItemResponse addItem(Integer inspectionId, CreateItemRequest request) {
        log.debug("Adding item to inspection with id: {}", inspectionId);
        InspectionValidators.validateCreateItem(request);

        var parent = findParent(inspectionId);
        requireEditable(parent);

        InspectionItem item = InspectionItemMapper.toEntity(request);
        item.setInspection(parent);

        return InspectionItemMapper.toResponse(inspectionItemRepository.save(item));
    }

    public Page<ItemResponse> listAll(Integer inspectionId, Pageable pageable) {
        log.debug("Listing items for inspection with id: {}", inspectionId);
        findParent(inspectionId);

        return inspectionItemRepository.findByInspectionId(inspectionId, pageable)
                .map(InspectionItemMapper::toResponse);
    }

    @Transactional
    public ItemResponse update(Integer inspectionId, Integer itemId, UpdateItemRequest request) {
        log.debug("Updating item with id: {} for inspection with id: {}", itemId, inspectionId);
        InspectionValidators.validateUpdateItem(request);

        var parent = findParent(inspectionId);
        requireEditable(parent);

        var item = inspectionItemRepository.findByInspectionIdAndId(inspectionId, itemId)
                .orElseThrow(() -> new NotFoundException("Item with id " + itemId + " not found for inspection " + inspectionId));

        item.setCategory(request.category());
        item.setNote(request.note());
        item.setSeverity(request.severity());

        return InspectionItemMapper.toResponse(inspectionItemRepository.save(item));
    }

    @Transactional
    public void delete(Integer inspectionId, Integer itemId) {
        log.debug("Deleting item with id: {} for inspection with id: {}", itemId, inspectionId);
        var parent = findParent(inspectionId);
        requireEditable(parent);

        var exists = inspectionItemRepository.findByInspectionIdAndId(inspectionId, itemId).isPresent();
        if (!exists) {
            throw new NotFoundException("Item with id " + itemId + " not found for inspection " + inspectionId);
        }

        inspectionItemRepository.hardDeleteByItemId(itemId);
    }

    private Inspection findParent(Integer inspectionId) {
        return inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new NotFoundException("Inspection with id " + inspectionId + " not found"));
    }

    private void requireEditable(Inspection inspection) {
        if (inspection.getStatus() != Status.DRAFT) {
            throw new ConflictException("Inspection is not editable in status: " + inspection.getStatus());
        }
    }
}
