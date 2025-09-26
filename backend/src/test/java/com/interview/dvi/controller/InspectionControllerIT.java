package com.interview.dvi.controller;

import com.interview.dvi.testsupport.utils.TestDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import com.interview.dvi.testsupport.BaseIntegrationTest;
import com.interview.dvi.model.dto.CreateInspectionRequest;
import com.interview.dvi.model.dto.InspectionResponse;
import com.interview.dvi.model.dto.UpdateInspectionRequest;
import com.interview.dvi.model.entities.Inspection;
import com.interview.dvi.model.enums.Status;
import com.interview.dvi.repository.InspectionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static com.interview.dvi.testsupport.utils.TestDataUtils.TEST_VIN1;
import static com.interview.dvi.testsupport.utils.TestDataUtils.getTestInspectionDraft;


public class InspectionControllerIT extends BaseIntegrationTest {

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void given_no_token_when_call_protected_then_401() {
        webTestClient.get().uri("/api/v1/inspections?page=0&size=5")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void given_no_data_when_create_inspection_then_data_created() {
        var createInspectionRequest = new CreateInspectionRequest(TEST_VIN1,
                                                        "Initial Inspection started.",
                                                        null,
                                                        null);

        assertEquals(0, inspectionRepository.count());

        InspectionResponse response = authenticated(webTestClient
                .post()
                .uri("/api/v1/inspections")
                .bodyValue(createInspectionRequest),
                TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InspectionResponse.class).returnResult().getResponseBody();

""        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals(TEST_VIN1, response.vin());
        assertEquals(Status.DRAFT, response.status());

        assertEquals(1, inspectionRepository.count());
        var savedEntity = inspectionRepository.findAll().getFirst();

        assertEquals(1, savedEntity.getId());
        assertEquals(TEST_VIN1, savedEntity.getVin());
        assertEquals(Status.DRAFT, savedEntity.getStatus());
        assertEquals("Initial Inspection started.", savedEntity.getNote());
    }

    @Test
    void given_existing_inspection_when_get_by_id_then_return_inspection() {
        Inspection inspection = new Inspection();
        inspection.setVin(TEST_VIN1);
        inspection.setStatus(Status.DRAFT);
        inspection.setNote("Initial Inspection started.");

        Inspection saved = inspectionRepository.save(inspection);

        InspectionResponse fetchedResponse = authenticated(webTestClient
                .get()
                .uri("/api/v1/inspections/{id}", saved.getId()),
                    TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InspectionResponse.class).returnResult().getResponseBody();

        assertNotNull(fetchedResponse);
        assertEquals(1, fetchedResponse.id());
        assertEquals(TEST_VIN1, fetchedResponse.vin());
        assertEquals(Status.DRAFT, fetchedResponse.status());
        assertEquals("Initial Inspection started.", fetchedResponse.note());
    }

    @Test
    void given_existing_inspection_when_update_then_fields_changed() {
        Inspection inspection = inspectionRepository.save(getTestInspectionDraft(TEST_VIN1));

        var updateRequest = new UpdateInspectionRequest("New Note", "Repair needed", BigDecimal.valueOf(200));

        InspectionResponse response = authenticated(webTestClient.put()
                .uri("/api/v1/inspections/{id}", inspection.getId())
                .bodyValue(updateRequest),
                    TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InspectionResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(response);
        assertEquals("New Note", response.note());
        assertEquals("Repair needed", response.recommendation());
        assertEquals(BigDecimal.valueOf(200), response.estimatedCost());
    }

    @Test
    void given_existing_inspection_when_delete_then_removed() {
        Inspection inspection = inspectionRepository.save(getTestInspectionDraft(TEST_VIN1));

        authenticated( webTestClient.delete()
                .uri("/api/v1/inspections/{id}", inspection.getId()),
                    TestDataUtils.USER_ID_ADMIN, TestDataUtils.ADMIN_ROLE)
                .exchange()
                .expectStatus().isNoContent();

        assertEquals(0, inspectionRepository.count());
    }

    @Test
    void given_existing_inspection_when_submit_then_status_submitted_and_response() {
        Inspection inspection = inspectionRepository.save(getTestInspectionDraft(TEST_VIN1));

        String response = authenticated(webTestClient
                .post()
                .uri("/api/v1/inspections/{id}/submit", inspection.getId()),
                    TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult().getResponseBody();

        assertNotNull(response);
        Inspection submitted = inspectionRepository.findById(inspection.getId()).orElseThrow();
        assertEquals(Status.SUBMITTED, submitted.getStatus());
    }
}
