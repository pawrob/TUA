package pl.lodz.pl.it.ssbd2021.ssbd05.mok.cdi.endpoints;

import io.swagger.annotations.Api;
import pl.lodz.pl.it.ssbd2021.ssbd05.cdi.endpoints.AbstractEndpoint;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.UserWithAccessLevelDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AccessLevelNotFound;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.AbstractAppExceptionEndpointInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers.EntertainerManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.AccessLevelConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.EntertainerConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.UserConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EndpointLoggerInterceptor;

import javax.ejb.AccessLocalException;
import javax.ejb.EJBAccessException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Endpoint CDI odpowiadający za operacje na encjach użytkownika z ograniczeniem dostępności do poziomu dostępu "ENTERTAINER".
 */
@Path("entertainer")
@RequestScoped
@Api(value = "Entertainer Endpoint")
@Interceptors({AbstractAppExceptionEndpointInterceptor.class, EndpointLoggerInterceptor.class})
@TransactionAttribute(TransactionAttributeType.NEVER)
public class EntertainerEndpoint extends AbstractEndpoint {

    @Inject
    private EntertainerManagerLocal entertainerManager;

    @Override
    protected ManagerLocal getManager() {
        return entertainerManager;
    }

    /**
     * Metoda endpointu pozwalająca na dezaktywację konta z poziomem dostępu Entertainer.
     *
     * @param id - identyfikator konta, ktore ma byc dezaktywowane
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * 412: encja została zedytowana lub usunięta w trakcie wykonywania
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("deactivate/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateEntertainer(@PathParam("id") Long id) throws AbstractAppException {
        UserEntity user = null;
        try {
            user = retrying(() -> entertainerManager.deactivateEntertainerAccount(id));
        } catch (AccessLevelNotFound accessLevelNotFound) {
            accessLevelNotFound.printStackTrace();
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(accessLevelNotFound.getMessage())
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
        return Response
                .ok()
                .entity(UserConverter.userWithAccessLevelDTOFromEntity(user))
                .build();
    }

    /**
     * Metoda endpointu pozwalająca na reaktywację konta z poziomem dostępu Entertainer.
     *
     * @param id - identyfikator konta, ktore ma byc reaktywowane
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * 412: encja została zedytowana lub usunięta w trakcie wykonywania
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("reactivate/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reactivateEntertainer(@PathParam("id") Long id) throws AbstractAppException {
        UserEntity user = null;
        try {
            user = retrying(() -> entertainerManager.reactivateEntertainerAccount(id));
        } catch (AccessLevelNotFound accessLevelNotFound) {
            accessLevelNotFound.printStackTrace();
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(accessLevelNotFound.getMessage())
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
        return Response
                .ok()
                .entity(UserConverter.userWithAccessLevelDTOFromEntity(user))
                .build();
    }

    @PUT
    @Path("accessLevelChange/{id}/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(EndpointLoggerInterceptor.class)
    public Response changeEntertainerAccessLevelStatus(@PathParam("id") Long id, @PathParam("status") boolean status) throws AbstractAppException {
        AccessLevelEntity accessLevel = null;
        try {
            accessLevel = entertainerManager.changeEntertainerAccessLevelStatus(id, status);
        } catch (AccessLevelNotFound accessLevelNotFound) {
            accessLevelNotFound.printStackTrace();
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(accessLevelNotFound.getMessage())
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
        return Response
                .ok()
                .entity(AccessLevelConverter.AccessLevelDTOFromEntity(accessLevel))
                .build();
    }

    /**
     * Metoda endpointu pozwalająca na utworzenie nowego uzytkownika o roli Entertainer.
     *
     * @param entertainer obiekt nowego uzytkownika
     * @param password    haslo uzytkownika
     * @return 201: uzytkownik zostal utworzony, body - utworzony uzytkownik;
     * 409: Conflict, body - opis bledu
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * 412: encja została zedytowana lub usunięta w trakcie wykonywania
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @POST
    @Path("add/{password}")
    @Consumes(APPLICATION_JSON)
    public Response createEntertainer(@Valid EntertainerDTO entertainer, @Valid @Pattern(regexp = "^(?!.*[\\s])^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@$%^&*]).{8,}$") @PathParam("password") String password)
            throws AbstractAppException {
        try {
            UserWithAccessLevelDTO created = UserConverter.userWithAccessLevelDTOFromEntity(
                    retrying(() -> entertainerManager.createEnterainer(
                            EntertainerConverter.createNewEntertainerEntityFromDTO(entertainer, password))
                    ));
            return Response
                    .status(Response.Status.CREATED)
                    .entity(created)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
}
