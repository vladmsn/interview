package com.interview.dvi.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.interview.dvi.model.dto.CreateItemRequest;
import com.interview.dvi.model.dto.ItemResponse;
import com.interview.dvi.model.dto.UpdateItemRequest;

@Tag(name = "Inspection Items", description = "Item management under an inspection")
public interface InspectionItemsAPI {

    @Operation(summary = "Add item to a DRAFT inspection",
                responses = {
                    @ApiResponse(responseCode = "201", description = "Item created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Inspection Not Found")
                })
    ItemResponse createItem(Integer inspectionId, CreateItemRequest request);

    @Operation(summary = "List items by inspection (paginated)",
                responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Inspection Not Found")
                })
    PagedModel<ItemResponse> listItems(Integer inspectionId, Pageable pageable);

    @Operation(summary = "Update item (only in DRAFT)",
                responses = {
                    @ApiResponse(responseCode = "200", description = "Item updated"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Inspection or Item Not Found")
                })
    ItemResponse updateItem(Integer inspectionId, Integer itemId, UpdateItemRequest request);

    @Operation(summary = "Delete item (only in DRAFT)",
                responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "404", description = "Inspection or Item Not Found")
                })
    void deleteItem(Integer inspectionId, Integer itemId);
}
