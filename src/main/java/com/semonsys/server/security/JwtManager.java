package com.semonsys.server.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.security.PrivateKey;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Stateless
@Log
public class JwtManager {
    public static final int SECONDS_PER_TEN_DAYS = 3600 * 24 * 10;
    public static final int SECONDS_IN_FIVE_HOURS = 5 * 60 * 60;
    public static final int MILLISECONDS_IN_SECOND = 1000;
    private static final String CLAIM_ROLES = "groups";
    private static final String ISSUER = "quickstart-jwt-issuer";
    private static final String AUDIENCE = "jwt-audience";
    private static final String REFRESH_TOKEN = "ROLE_REFRESH_TOKEN";
    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        privateKey = KeyLoader.load();
    }

    public Map<String, Object> getClaims(final String token) throws ParseException {
        JWT j = JWTParser.parse(token);
        return j.getJWTClaimsSet().getClaims();
    }

    public String createAccessToken(final String subject, final String[] roles) {
        JsonArrayBuilder rolesBuilder = Json.createArrayBuilder();
        Arrays.stream(roles).forEach(rolesBuilder::add);
        JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
            .add("sub", subject)
            .add("iss", ISSUER)
            .add("aud", AUDIENCE)
            .add(CLAIM_ROLES, rolesBuilder.build())
            .add("exp", System.currentTimeMillis() / MILLISECONDS_IN_SECOND
                + SECONDS_IN_FIVE_HOURS);
        return buildToken(claimsBuilder);
    }

    public String createRefreshToken(final String username) {
        JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
            .add("sub", username)
            .add("iss", ISSUER)
            .add("aud", AUDIENCE)
            .add("id", UUID.randomUUID().toString())
            .add("scopes", REFRESH_TOKEN)
            .add("exp", System.currentTimeMillis() / MILLISECONDS_IN_SECOND
                + SECONDS_PER_TEN_DAYS);
        return buildToken(claimsBuilder);
    }

    private String buildToken(final JsonObjectBuilder claimsBuilder) {
        JWSSigner signer = new RSASSASigner(privateKey);
        JWSObject jwsObject = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.RS256)
            .type(new JOSEObjectType("jwt")).build(),
            new Payload(claimsBuilder.build().toString()));
        try {
            jwsObject.sign(signer);
        } catch (JOSEException e) {
            log.warning(e.getMessage());
        }
        return jwsObject.serialize();
    }
}
