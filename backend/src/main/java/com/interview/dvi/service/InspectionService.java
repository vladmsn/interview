package com.interview.dvi.service;

import com.interview.dvi.model.dto.UpdateInspectionRequest;
import com.interview.dvi.model.entities.Inspection;
import com.interview.dvi.model.enums.Status;
import com.interview.dvi.model.exceptions.ConflictException;
import com.interview.dvi.model.exceptions.NotFoundException;
import com.interview.dvi.model.mapper.InspectionMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.interview.dvi.model.dto.CreateInspectionRequest;
import com.interview.dvi.model.dto.InspectionResponse;
import com.interview.dvi.repository.InspectionRepository;
import com.interview.dvi.repository.InspectionItemRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final InspectionItemRepository itemRepository;

    @Transactional
    public InspectionResponse create(CreateInspectionRequest createInspectionRequest) {
        log.info("Creating inspection for VIN: {}", createInspectionRequest.vin());
        Inspection inspection = InspectionMapper.toEntity(createInspectionRequest);
        var saved = inspectionRepository.save(inspection);

        return InspectionMapper.toResponse(saved);
    }

    public InspectionResponse searchById(Integer inspectionId) {
        log.info("Searching inspection for ID: {}", inspectionId);
        var inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + inspectionId));

        return InspectionMapper.toResponse(inspection);
    }

    public Page<InspectionResponse> searchAll(Pageable pageable, String vin) {
        var page = Optional.ofNullable(vin)
                .map(v -> inspectionRepository.findByVin(v, pageable))
                .orElseGet(() -> inspectionRepository.findAll(pageable));

        return page.map(InspectionMapper::toResponse);
    }

    @Transactional
    public InspectionResponse update(Integer id, UpdateInspectionRequest updateInspectionRequest) {
        log.debug("Updating inspection with id: {}, request: {}", id, updateInspectionRequest);
        var inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + id));

        requireEditable(inspection);

        inspection.setNote(updateInspectionRequest.note());
        inspection.setRecommendation(updateInspectionRequest.recommendation());
        inspection.setEstimatedCost(updateInspectionRequest.estimatedCost());

        var updated = inspectionRepository.save(inspection);
        log.debug("Updated inspection with id: {}", updated.getId());
        return InspectionMapper.toResponse(updated);
    }

    @Transactional
    public void delete(Integer id) {
        log.debug("Deleting inspection with id: {}", id);
        var inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + id));

        if (inspection.getStatus() != Status.DRAFT) {
            throw new ConflictException("Inspection with id " + inspection.getId() + " is not deletable in status: " + inspection.getStatus());
        }

        itemRepository.hardDeleteAllByInspectionId(inspection.getId());
        inspectionRepository.delete(inspection);
    }

    @Transactional
    public void archive(Integer id) {
        log.debug("Archiving inspection with id: {}", id);
        var inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + id));

        inspectionRepository.delete(inspection);
    }

    @Transactional
    public String submit(Integer id) {
        log.debug("Submitting inspection with id: {}", id);
        var inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection not found with id: " + id));
        requireEditable(inspection);

        inspection.setStatus(Status.SUBMITTED);
        inspectionRepository.save(inspection);
        return "SUBMITTED";
    }

    private void requireEditable(Inspection inspection) {
        if (inspection.getStatus() != Status.DRAFT) {
            throw new ConflictException("Inspection with id " + inspection.getId() + " is not editable in status: " + inspection.getStatus());
        }
    }
}
