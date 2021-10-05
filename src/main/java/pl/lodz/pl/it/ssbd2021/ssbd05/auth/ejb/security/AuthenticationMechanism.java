package pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.security;

import com.nimbusds.jwt.SignedJWT;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.JwtGeneratorVerifier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

/**
 * Klasa implementująca mechanizm obsługi żądań HTTP wpływających do kontenera.
 */
@ApplicationScoped
public class AuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    JwtGeneratorVerifier jwtGeneratorVerifier;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";
    private static final String[] EXCLUDED_PATH = {
            "resources/clients/add/.*",
            "resources/auth",
            "resources/user/requestResetPassword/.*",
            "resources/user/resetPassword/.*",
            "resources/user/verify/.*",
            "resources/user/logChangeAccessLevel",
            "resources/user/failedLogin/.*",
            "resources/user/failedLogin",
            "resources/user/successLogin/.*",
            "resources/user/successLogin",
            "resources/moo/offer/allActive",
    };

    /**
     * Metoda pozwalająca na walidacje przychodzących żądań.
     *
     * @param request - przychodzące żądanie
     * @param response - odpowiedź serwera aplikacyjnego
     * @param httpMessageContext - kontekst żądania
     * @return SUCCESS -
     * SEND_CONTINUE -
     * NOT_DONE -
     * SEND_FAILURE -
     * @throws AuthenticationException
     */
    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {

        if (!request.getRequestURL().toString().matches(".*resources/.*$")) {
            return httpMessageContext.doNothing();
        }

        for (String path : EXCLUDED_PATH) {
            if (request.getRequestURL().toString().matches(".*" + path + "$")) {
                return httpMessageContext.doNothing();
            }
        }


        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (null == authorizationHeader || !authorizationHeader.startsWith(BEARER)) {
            return httpMessageContext.responseUnauthorized();
        }
        String jwtSerializedToken = authorizationHeader.substring((BEARER.length())).trim();
        if (jwtGeneratorVerifier.validateJWTSignature(jwtSerializedToken)) {
            try {
                SignedJWT jwtToken = SignedJWT.parse(jwtSerializedToken);
                String login = jwtToken.getJWTClaimsSet().getSubject();
                String groups = jwtToken.getJWTClaimsSet().getStringClaim("auth");
                Date expirationTime = (Date) (jwtToken.getJWTClaimsSet().getClaim("exp"));
                boolean tokenExpired = new Date().after(expirationTime);
                if (tokenExpired) {
                    return httpMessageContext.responseUnauthorized();
                }
                return httpMessageContext.notifyContainerAboutLogin(login, new HashSet<>(Arrays.asList(groups.split(","))));
            } catch (ParseException e) {
                return httpMessageContext.responseUnauthorized();
            }
        }
        return httpMessageContext.responseUnauthorized();
    }
}
