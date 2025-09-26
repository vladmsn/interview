package com.interview.dvi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.interview.dvi.model.dto.CreateItemRequest;
import com.interview.dvi.model.dto.ItemResponse;
import com.interview.dvi.model.dto.UpdateItemRequest;
import com.interview.dvi.model.entities.Inspection;
import com.interview.dvi.model.enums.Severity;
import com.interview.dvi.model.enums.Status;
import com.interview.dvi.repository.InspectionRepository;
import com.interview.dvi.testsupport.BaseIntegrationTest;
import com.interview.dvi.testsupport.utils.TestDataUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static com.interview.dvi.testsupport.utils.TestDataUtils.TEST_VIN1;
import static com.interview.dvi.testsupport.utils.TestDataUtils.getTestInspectionDraft;

public class InspectionItemControllerIT extends BaseIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Test
    void given_no_token_when_call_items_then_401() {
        webTestClient.get()
                .uri("/api/v1/inspections/{inspectionId}/items?page=0&size=5", 1)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void given_draft_inspection_when_create_item_then_created() {
        Inspection parent = inspectionRepository.save(getTestInspectionDraft(TEST_VIN1));

        var createReq = new CreateItemRequest("Pads below spec; rotor lip present",
                "Brakes",
                Severity.HIGH);

        ItemResponse created = authenticated(webTestClient.post()
                        .uri("/api/v1/inspections/{id}/items", parent.getId())
                        .bodyValue(createReq),
                TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ItemResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);
        assertNotNull(created.id());
        assertEquals("Brakes", created.category());
        assertEquals("Pads below spec; rotor lip present", created.note());
        assertEquals(Severity.HIGH, created.severity());
    }

    @Test
    void given_existing_item_when_update_then_updated() {
        Inspection parent = inspectionRepository.save(getTestInspectionDraft(TEST_VIN1));

        var createReq = new CreateItemRequest("Pads below spec; rotor lip present",
                "Brakes",
                Severity.HIGH
        );

        ItemResponse created = authenticated(webTestClient.post()
                        .uri("/api/v1/inspections/{id}/items", parent.getId())
                        .bodyValue(createReq),
                TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ItemResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(created);
        Integer itemId = created.id();

        var updateReq = new UpdateItemRequest("Rear left at 2/32\" — replace soon",
                "Tires",
                Severity.HIGH
        );

        ItemResponse updated = authenticated(webTestClient.put()
                        .uri("/api/v1/inspections/{iid}/items/{itemId}", parent.getId(), itemId)
                        .bodyValue(updateReq),
                TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ItemResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(updated);
        assertEquals(itemId, updated.id());
        assertEquals("Tires", updated.category());
        assertEquals("Rear left at 2/32\" — replace soon", updated.note());
        assertEquals(Severity.HIGH, updated.severity());
    }

    @Test
    void given_existing_item_when_delete_then_no_content_and_absent_from_list() {
        Inspection parent = inspectionRepository.save(getTestInspectionDraft(TEST_VIN1));

        var createReq = new CreateItemRequest("Pads below spec; rotor lip present",
                "Brakes",
                Severity.HIGH
        );

        ItemResponse created = authenticated(webTestClient.post()
                        .uri("/api/v1/inspections/{id}/items", parent.getId())
                        .bodyValue(createReq),
                TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ItemResponse.class)
                .returnResult().getResponseBody();

        assertNotNull(created);
        Integer itemId = created.id();

        authenticated(webTestClient.delete()
                        .uri("/api/v1/inspections/{iid}/items/{itemId}", parent.getId(), itemId),
                TestDataUtils.USER_ID_ADMIN, TestDataUtils.ADMIN_ROLE)
                .exchange()
                .expectStatus().isNoContent();

        authenticated(webTestClient.get()
                        .uri("/api/v1/inspections/{id}/items?page=0&size=5", parent.getId()),
                TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(0);
    }

    @Test
    void given_submitted_inspection_when_create_item_then_409() {
        Inspection parent = new Inspection();
        parent.setVin(TEST_VIN1);
        parent.setStatus(Status.SUBMITTED);
        parent = inspectionRepository.save(parent);

        var createReq = new CreateItemRequest("Pads below spec; rotor lip present",
                "Brakes",
                Severity.HIGH
        );

        authenticated(webTestClient.post()
                        .uri("/api/v1/inspections/{id}/items", parent.getId())
                        .bodyValue(createReq),
                TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void given_missing_inspection_when_list_items_then_404() {
        authenticated(webTestClient.get()
                        .uri("/api/v1/inspections/{id}/items?page=0&size=5", 99999),
                TestDataUtils.USER_ID_TECH_1, TestDataUtils.STAFF_ROLE)
                .exchange()
                .expectStatus().isNotFound();
    }
}
