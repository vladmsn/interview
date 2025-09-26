package com.interview.dvi.controller;

import com.interview.dvi.service.ApprovalTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/public/inspections/decision")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalTokenService approvalTokenService;

    @RequestMapping( produces = MediaType.TEXT_HTML_VALUE)
    public String decision(@RequestParam("token") String token) {
        String newStatus = approvalTokenService.consumeDecisionToken(token);

        return String.format("""
                                <!doctype html>
                                <meta charset="utf-8"><title>Decision recorded</title>
                                <body style="font-family:sans-serif;max-width:640px;margin:40px auto;line-height:1.4">
                                  <h2>Thanks!</h2>
                                  <p>Your decision was recorded as <b>%s</b>.</p>
                                </body>
                            """, newStatus);
    }

}
