package com.interview.dvi.testsupport;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

public class TokenSigner {
    private static RSAPrivateKey key;

    private static RSAPrivateKey privateKey() {
        if (key != null) return key;
        try {
            var pemBytes = new ClassPathResource("keys/private.pem").getContentAsByteArray();
            var pem = new String(pemBytes, StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----","")
                    .replace("-----END PRIVATE KEY-----","")
                    .replaceAll("\\s","");
            key = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
            return key;
        } catch (Exception e) {
            throw new RuntimeException("Load test private key failed", e);
        }
    }

    public static String bearer(String sub, String... roles) {
        try {
            var now = Instant.now();
            var claims = new JWTClaimsSet.Builder()
                    .subject(sub)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plus(3650, ChronoUnit.DAYS))) // ~10 years
                    .claim("roles", Arrays.asList(roles))
                    .build();

            var header = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
            var jwt = new SignedJWT(header, claims);
            jwt.sign(new RSASSASigner(privateKey()));
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Sign test token failed", e);
        }
    }
}
