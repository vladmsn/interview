package com.interview.dvi.controller;

import com.interview.dvi.api.v1.InspectionItemsAPI;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.interview.dvi.model.dto.CreateItemRequest;
import com.interview.dvi.model.dto.ItemResponse;
import com.interview.dvi.model.dto.UpdateItemRequest;
import com.interview.dvi.service.InspectionItemService;

@RestController
@RequestMapping("/api/v1/inspections/{inspectionId}/items")
@RequiredArgsConstructor
public class InspectionItemController implements InspectionItemsAPI {
    private final InspectionItemService itemService;

    @Override
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STAFF')")
    public ItemResponse createItem(@PathVariable Integer inspectionId,
                                   @RequestBody CreateItemRequest request) {
        return itemService.addItem(inspectionId, request);
    }

    @Override
    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
    public PagedModel<ItemResponse> listItems(@PathVariable Integer inspectionId, Pageable pageable) {
        var page = itemService.listAll(inspectionId, pageable);
        return new PagedModel<>(page);
    }

    @Override
    @PutMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ItemResponse updateItem(@PathVariable Integer inspectionId,
                                   @PathVariable Integer itemId,
                                   @RequestBody UpdateItemRequest request) {
        return itemService.update(inspectionId, itemId, request);
    }

    @Override
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deleteItem(@PathVariable Integer inspectionId, @PathVariable Integer itemId) {
        itemService.delete(inspectionId, itemId);
    }
}
