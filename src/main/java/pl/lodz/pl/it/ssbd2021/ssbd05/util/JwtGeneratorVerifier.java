package pl.lodz.pl.it.ssbd2021.ssbd05.util;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import java.text.ParseException;
import java.util.Date;

@LocalBean
public class JwtGeneratorVerifier {


    @Resource(name = "jwtSecret")
    private String SECRET;
    @Resource(name = "jwtTimeout")
    private long JWT_TIMEOUT; //millis

    public String generateJWTString(CredentialValidationResult credential) {
        try {
            JWSSigner signer = new MACSigner(SECRET);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(credential.getCallerPrincipal().getName())
                    .claim("auth", String.join(",", credential.getCallerGroups()))
                    .issuer("ssbd05")
                    .expirationTime(new Date(new Date().getTime() + JWT_TIMEOUT))
                    .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException ex) {
            return "JWT failure" + ex;
        }
    }

    public String refreshJWT(String currentJWT) {
        try {
            SignedJWT oldJWT = SignedJWT.parse(currentJWT);
            JWTClaimsSet oldClaimsSet = oldJWT.getJWTClaimsSet();
            JWSSigner signer = new MACSigner(SECRET);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(oldClaimsSet.getSubject())
                    .claim("auth", oldClaimsSet.getClaim("auth"))
                    .issuer(oldClaimsSet.getIssuer())
                    .expirationTime(new Date(new Date().getTime() + JWT_TIMEOUT))
                    .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException | ParseException ex) {
            return "JWT failure";
        }
    }

    public boolean validateJWTSignature(String jwtToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwtToken);
            JWSVerifier verifier = new MACVerifier(SECRET);
            return signedJWT.verify(verifier);
        } catch (JOSEException | ParseException ex) {
            return false;
        }
    }

    private JwtGeneratorVerifier() {

    }

}
