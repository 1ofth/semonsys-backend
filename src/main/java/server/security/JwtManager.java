package server.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import lombok.extern.java.Log;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
@Log
public class JwtManager {
    static {
        char[] password = "secret".toCharArray();
        String alias = "alias";
        PrivateKey pk = null;
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            log.warning(e.getMessage());
        }
        String configDir = System.getProperty("jboss.server.config.dir");
        String keystorePath = configDir + File.separator + "jwt.keystore";
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            assert ks != null;
            ks.load(fis, password);
            Key key = ks.getKey(alias, password);
            if (key instanceof PrivateKey) {
                pk = (PrivateKey) key;
            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
        privateKey = pk;
    }

    private static final PrivateKey privateKey;
    private static final String CLAIM_ROLES = "groups";
    private static final String ISSUER = "quickstart-jwt-issuer";
    private static final String AUDIENCE = "jwt-audience";
    private static final String REFRESH_TOKEN = "ROLE_REFRESH_TOKEN";

    public Map<String, Object> getClaims(String token) throws ParseException {
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
                .add("exp", (System.currentTimeMillis() / 1000) + 14400);
        return buildToken(claimsBuilder);
    }

    public String createRefreshToken(final String username) {
        JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
                .add("sub", username)
                .add("iss", ISSUER)
                .add("aud", AUDIENCE)
                .add("id", UUID.randomUUID().toString())
                .add("scopes", REFRESH_TOKEN)
                .add("exp", (System.currentTimeMillis() / 1000) + 3600 * 24 * 10);
        return buildToken(claimsBuilder);
    }

    private String buildToken(JsonObjectBuilder claimsBuilder) {
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
