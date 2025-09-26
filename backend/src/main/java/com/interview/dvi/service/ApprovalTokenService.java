package com.interview.dvi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.dvi.model.dto.ReviewRequest;
import com.interview.dvi.model.enums.Status;
import com.interview.dvi.model.exceptions.ConflictException;
import com.interview.dvi.model.exceptions.NotFoundException;
import com.interview.dvi.repository.InspectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.interview.dvi.config.properties.ApprovalProperties;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalTokenService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64D = Base64.getUrlDecoder();

    private final ApprovalProperties props;
    private final InspectionRepository inspections;

    public Map<String, String> generateShareLinks(Integer inspectionId, Long overrideTtlMinutes) {
        var minutes = (overrideTtlMinutes == null || overrideTtlMinutes <= 0)
                ? props.ttl().toMinutes()
                : overrideTtlMinutes;
        var exp = Instant.now().plus(minutes, ChronoUnit.MINUTES);

        var approve = createToken(inspectionId, "APPROVE", exp);
        var reject  = createToken(inspectionId, "REJECT",  exp);

        return Map.of(
                "approveUrl", props.baseUrl() + "/public/inspections/decision?token=" + approve,
                "rejectUrl",  props.baseUrl() + "/public/inspections/decision?token=" + reject,
                "expiresAt",  exp.toString()
        );
    }

    @Transactional
    public String consumeDecisionToken(String token) {
        ReviewRequest req = verifyToken(token);
        var decision = "APPROVE".equals(req.decision()) ? Status.APPROVED : Status.REJECTED;
        applyCustomerDecision(req.inspectionId(), decision);
        return decision.name();
    }

    @Transactional
    void applyCustomerDecision(Integer id, Status newStatus) {
        var ins = inspections.findById(id)
                .orElseThrow(() -> new NotFoundException("Inspection " + id + " not found"));

        if (newStatus != Status.APPROVED && newStatus != Status.REJECTED) {
            throw new ConflictException("Decision must be APPROVED or REJECTED");
        }

        switch (ins.getStatus()) {
            case SUBMITTED -> {
                ins.setStatus(newStatus);
                inspections.save(ins);
            }
            case APPROVED -> {
                if (newStatus != Status.APPROVED) {
                    throw new ConflictException("Already APPROVED");
                }
            }
            case REJECTED -> {
                if (newStatus != Status.REJECTED) {
                    throw new ConflictException("Already REJECTED");
                }
            }
            default -> throw new ConflictException("Only SUBMITTED inspections can be decided");
        }
    }

    // ------- Token utils ( HMAC-SHA256 + Base64 ) -------

    private String createToken(Integer inspectionId, String decision, Instant exp) {
        try {
            var json = MAPPER.writeValueAsBytes(new ReviewRequest(inspectionId, decision, exp.getEpochSecond()));
            var body = B64.encodeToString(json);
            var sig = sign(body);
            return body + "." + sig;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create approval token", e);
        }
    }

    private ReviewRequest verifyToken(String token) {
        try {
            var parts = token.split("\\.");
            if (parts.length != 2) throw new IllegalArgumentException("Malformed token");
            var body = parts[0];
            var sig  = parts[1];

            if (!constantTimeEquals(sig, sign(body))) throw new IllegalArgumentException("Bad signature");

            var p = MAPPER.readValue(B64D.decode(body), ReviewRequest.class);
            if (Instant.now().getEpochSecond() > p.expiryEpochSeconds()) {
                throw new IllegalArgumentException("Expired");
            }
            if (!"APPROVE".equals(p.decision()) && !"REJECT".equals(p.decision())) {
                throw new IllegalArgumentException("Invalid decision");
            }
            return p;
        } catch (IllegalArgumentException e) { throw e; }
        catch (Exception e) { throw new IllegalArgumentException("Invalid token", e); }
    }

    private String sign(String body) throws Exception {
        var mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(getHmacKey(), "HmacSHA256"));
        return B64.encodeToString(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int r = 0; for (int i = 0; i < a.length(); i++) r |= a.charAt(i) ^ b.charAt(i);
        return r == 0;
    }

    private byte[] getHmacKey() {
        return props.secret().getBytes(StandardCharsets.UTF_8);
    }
}
