package com.interview.dvi.testsupport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;


@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @Autowired
    private DatabaseTestHelper databaseHelper;

    @BeforeEach
    void beforeEach() {
        databaseHelper.resetDatabase();
    }

    /**
     * Adds an Authorization header with a bearer token to the given request spec.
     *
     * @param spec  the request spec to add the header to
     * @param sub   the subject (user ID) to include in the token
     * @param roles the roles to include in the token
     * @return the modified request spec
     */
    protected WebTestClient.RequestHeadersSpec<?> authenticated(WebTestClient.RequestHeadersSpec<?> spec,
                                                                String sub, String... roles) {
        return spec.headers(headers -> headers.setBearerAuth(TokenSigner.bearer(sub, roles)));
    }
}
