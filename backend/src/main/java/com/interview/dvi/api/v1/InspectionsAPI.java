package com.interview.dvi.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.interview.dvi.model.dto.CreateInspectionRequest;
import com.interview.dvi.model.dto.InspectionResponse;
import com.interview.dvi.model.dto.UpdateInspectionRequest;

import java.util.Map;

@Tag(name = "Inspections", description = "Inspection lifecycle & management API")
public interface InspectionsAPI {
    @Operation(summary = "Create a DRAFT inspection",
            responses = {
                @ApiResponse(responseCode = "201", description = "Inspection session created",
                        content = @Content(schema = @Schema(implementation = InspectionResponse.class))),
                @ApiResponse(responseCode = "400", description = "Bad Request")
            })
    InspectionResponse createInspection(CreateInspectionRequest request);

    @Operation(summary = "Get inspection by ID",
                responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = InspectionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Not Found")
                })
    InspectionResponse getInspectionById(Integer inspectionId);

    @Operation(summary = "List inspections (paginated, optional filtered by VIN)",
                responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = PagedModel.class)))
                })
    PagedModel<InspectionResponse> listInspections(Pageable pageable, String vin);

    @Operation(summary = "Update inspection summary (note, recommendation, estimatedCost)",
                responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = InspectionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
                })
    InspectionResponse updateInspectionById(Integer id, UpdateInspectionRequest request);

    @Operation(summary = "Delete inspection in DRAFT state by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "404", description = "Not Found"),
                    @ApiResponse(responseCode = "409", description = "Cannot delete non-DRAFT inspection. " +
                                                                    "Hint: Use archive endpoint instead")
            })
    void deleteInspectionById(Integer id);

    @Operation(summary = "Archive inspection by ID (soft delete)",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    void archiveInspectionById(Integer id);

    @Operation(summary = "Submit inspection for customer review",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = InspectionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    Map<String, String> submitInspection(Integer id);
}