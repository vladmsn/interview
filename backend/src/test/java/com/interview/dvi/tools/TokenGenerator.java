package com.interview.dvi.tools;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.core.io.ClassPathResource;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TokenGenerator {

    public static void main(String[] args) throws Exception {
        // 1) Load PKCS#8 private key from test resources
        byte[] pemBytes = new ClassPathResource("keys/private.pem").getContentAsByteArray();
        String pem = new String(pemBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        RSAPrivateKey privateKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));

        Instant exp = ZonedDateTime.of(2099, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC).toInstant();

        Map<String, String> tokens = Map.of(
                "ADMIN", sign("admin-001", List.of("ADMIN"), exp, privateKey),
                "STAFF", sign("tech-042", List.of("STAFF"), exp, privateKey),
                "USER",  sign("user-123", List.of("USER"),  exp, privateKey)
        );

        // 4) Print to console
        tokens.forEach((k,v) -> {
            System.out.println("=== " + k + " TOKEN ===");
            System.out.println(v);
            System.out.println();
        });

        try (FileWriter fw = new FileWriter("demo-tokens.txt")) {
            fw.write("# Demo JWTs (RS256) — DO NOT USE IN PRODUCTION\n\n");
            tokens.forEach((k,v) -> {
                try {
                    fw.write(k + "=\n" + v + "\n\n");
                } catch (Exception e) { throw new RuntimeException(e); }
            });
        }
        System.out.println("Wrote tokens to demo-tokens.txt");
    }

    private static String sign(String sub, List<String> roles, Instant exp, RSAPrivateKey privateKey) throws Exception {
        var claims = new JWTClaimsSet.Builder()
                .subject(sub)
                .issueTime(new Date())
                .expirationTime(Date.from(exp))
                .claim("roles", roles)
                .claim("iss", "dvi-local")
                .claim("aud", "dvi-api")
                .build();

        var header = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
        var jwt = new SignedJWT(header, claims);
        jwt.sign(new RSASSASigner(privateKey));
        return jwt.serialize();
    }
}
