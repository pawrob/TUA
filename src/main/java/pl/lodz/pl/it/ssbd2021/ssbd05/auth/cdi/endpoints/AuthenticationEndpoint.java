package pl.lodz.pl.it.ssbd2021.ssbd05.auth.cdi.endpoints;

import io.swagger.annotations.Api;
import pl.lodz.pl.it.ssbd2021.ssbd05.auth.ejb.managers.AuthenticationManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.cdi.endpoints.AbstractEndpoint;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ErrorDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.LoginDataDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.DatabaseErrorAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.IncorrectCredentialsAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.AbstractAppExceptionEndpointInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.JwtGeneratorVerifier;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EndpointLoggerInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.SessionLogger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Endpoint CDI odpowiadający za uwierzytelnienie i odświeżanie tokenu JWT
 */
@RequestScoped
@Path("auth")
@Api(value = "Authentication Endpoint")
@TransactionAttribute(TransactionAttributeType.NEVER)
public class AuthenticationEndpoint extends AbstractEndpoint {

    @Inject
    AuthenticationManagerLocal authenticationManager;

    @Inject
    JwtGeneratorVerifier jwtGeneratorVerifier;

    @Inject
    SessionLogger sessionLogger;

    /**
     * Metoda endpointowa uwierzytelniająca konto użytkownika.
     *
     * @param loginData - parametry uwierzytelnienia użytkownika
     * @return 202 - nowy token jwt w odpowiedzi
     * 401 - odpowiedz generowana w przypadku nie znalezienia uzytkownika o podanym loginie
     * @throws IncorrectCredentialsAppException rzucany w przypadku podania niepoprawnych poświadczeń
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Interceptors({EndpointLoggerInterceptor.class, AbstractAppExceptionEndpointInterceptor.class})
    public Response authenticate(@Valid LoginDataDTO loginData) throws AbstractAppException {
        UsernamePasswordCredential credential = new UsernamePasswordCredential(loginData.getLogin(), loginData.getPassword());
        CredentialValidationResult result = retrying(() ->
                authenticationManager.authenticate(credential)
        );
        retrying(() ->
                sessionLogger.saveAuthenticationInfo(result, loginData.getLogin())
        );
        if (result.getStatus() == CredentialValidationResult.Status.VALID) {

            return Response.accepted()
                    .type("application/jwt")
                    .entity(jwtGeneratorVerifier.generateJWTString(result))
                    .build();
        } else if (result.getStatus() == CredentialValidationResult.Status.INVALID) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorDTO.of(new IncorrectCredentialsAppException().getKey()))
                    .build();
        } else if (result.getStatus() == CredentialValidationResult.Status.NOT_VALIDATED) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorDTO.of(DatabaseErrorAppException.createDatabaseAppException().getKey()))
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     * Metoda pozwalająca na odświeżenie tokenu JWT
     *
     * @param auth - aktualny token JWT
     * @param securityContext - kontekst bezpieczeństwa
     * @return 202: nowy token JWT
     * 401: w przypadku przedłużenia sesji po upłynięciu czasu ważności
     */
    @POST
    @Path("/refresh")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    @Interceptors({EndpointLoggerInterceptor.class, AbstractAppExceptionEndpointInterceptor.class})
    public Response refreshToken(@HeaderParam("Authorization") @NotNull @NotEmpty String auth, @Context SecurityContext securityContext) throws AbstractAppException {
        if (authenticationManager.checkIfUserExists(securityContext.getUserPrincipal().getName())) {
            String jwtSerializedToken = auth.substring(("Bearer".length())).trim();
            return Response.accepted()
                    .type("application/jwt")
                    .entity(jwtGeneratorVerifier.refreshJWT(jwtSerializedToken))
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @Override
    protected ManagerLocal getManager() {
        return authenticationManager;
    }
}
