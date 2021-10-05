package pl.lodz.pl.it.ssbd2021.ssbd05.mok.cdi.endpoints;

import io.swagger.annotations.Api;
import pl.lodz.pl.it.ssbd2021.ssbd05.cdi.endpoints.AbstractEndpoint;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.PasswordDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.UserDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.UserWithAccessLevelDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ClientEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.IncorrectPasswordAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.NotAllowedAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.AbstractAppExceptionEndpointInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers.ClientManagerLocal;
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
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Endpoint CDI odpowiadający za operacje, które pozwalają na utworzenie i usunięcie konta użytkownika.
 */
@Path("clients")
@RequestScoped
@Api(value = "Client Endpoint")
@Interceptors({AbstractAppExceptionEndpointInterceptor.class, EndpointLoggerInterceptor.class})
@TransactionAttribute(TransactionAttributeType.NEVER)
public class ClientEndpoint extends AbstractEndpoint {

    @Inject
    private ClientManagerLocal clientManager;

    @Override
    protected ManagerLocal getManager() {
        return clientManager;
    }



    @GET
    @Path("{id}")
    @Produces(APPLICATION_JSON)
    public Response getClientInfo(@PathParam("id") Long id) throws AbstractAppException {
        UserEntity clientInfo = clientManager.getClientInfo(id);
        return Response
                .ok()
                .entity(UserConverter.userWithPersonalDataFromEntity(clientInfo))
                .build();
    }

    /**
     * Metoda endpointowa pozwalająca na stworzenie uzytkownika z poziomem dostepu client.
     *
     * @param user     - obiekt klasy userDTO przechowujący dane wejsciowe
     * @param password - haslo wymagane podczas tworzenia uzytkownika
     * @return 201: uzytkownik zostal utworzony, body - utworzony uzytkownik;
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * 412: encja została zedytowana lub usunięta w trakcie wykonywania
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @POST
    @Path("add/{password}")
    @Consumes(APPLICATION_JSON)
    public Response addClient(@Valid UserDTO user,@Valid @Pattern(regexp = "^(?!.*[\\s])^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@$%^&*]).{8,}$") @PathParam("password") String password) throws AbstractAppException {
        UserWithAccessLevelDTO createdUser = UserConverter.userWithAccessLevelDTOFromEntity(
                retrying(() -> clientManager.createUserAccountWithAccessLevel(
                        UserConverter.createNewUserEntityFromDTO(user, password), new ClientEntity()))
        );
        return Response
                .status(Response.Status.CREATED)
                .entity(createdUser)
                .build();
    }

    /**
     * Metoda endpointowa pozwalająca na usunięcie użytkownika.
     *
     * @param login - identyfikator użytkownika
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * 412: encja została zedytowana lub usunięta w trakcie wykonywania
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @DELETE
    @Path("user/{login}")
    @Consumes(APPLICATION_JSON)
    public Response deleteUser(@Valid PasswordDTO passwordDTO, @PathParam("login") String login) throws AbstractAppException {
        try {
            retrying(() -> {
                clientManager.deleteUser(login, passwordDTO.getPassword());
                return null;
            });
        } catch (IncorrectPasswordAppException e) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(e)
                    .build();
        } catch (NotAllowedAppException | EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
        return Response.noContent().status(Response.Status.OK).build();
    }
}
