package com.interview.dvi.controller;

import com.interview.dvi.api.v1.InspectionsAPI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import com.interview.dvi.model.dto.CreateInspectionRequest;
import com.interview.dvi.model.dto.InspectionResponse;
import com.interview.dvi.model.dto.UpdateInspectionRequest;
import com.interview.dvi.service.InspectionService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class InspectionController implements InspectionsAPI {

    private final InspectionService inspectionService;

    @Override
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('STAFF')")
    public InspectionResponse createInspection(@RequestBody CreateInspectionRequest request) {
        return inspectionService.create(request);
    }

    @Override
    @GetMapping("/{inspectionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public InspectionResponse getInspectionById(@PathVariable Integer inspectionId) {
        return inspectionService.searchById(inspectionId);
    }

    @Override
    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PagedModel<InspectionResponse> listInspections(Pageable pageable,
                                                          @RequestParam(required = false) String vin) {
        var page = inspectionService.searchAll(pageable, vin);
        return new PagedModel<>(page);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public InspectionResponse updateInspectionById(@PathVariable Integer id,
                                                   @RequestBody UpdateInspectionRequest request) {
        return inspectionService.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteInspectionById(@PathVariable Integer id) {
        inspectionService.delete(id);
    }

    @Override
    @PostMapping("/{id}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void archiveInspectionById(@PathVariable Integer id) {
        inspectionService.archive(id);
    }

    @Override
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STAFF')")
    public Map<String, String> submitInspection(@PathVariable Integer id) {
        return inspectionService.submit(id);
    }
}
