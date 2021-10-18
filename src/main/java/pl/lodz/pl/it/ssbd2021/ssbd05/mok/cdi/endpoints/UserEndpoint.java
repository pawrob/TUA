package pl.lodz.pl.it.ssbd2021.ssbd05.mok.cdi.endpoints;

import io.swagger.annotations.Api;
import pl.lodz.pl.it.ssbd2021.ssbd05.cdi.endpoints.AbstractEndpoint;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.SessionLogEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AccessLevelNotFound;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.NotAllowedAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.SessionLogNotFoundException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.AbstractAppExceptionEndpointInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers.UserManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.AccessLevelConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.PersonalDataConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.SessionLogConverter;
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
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Endpoint CDI odpowiadający za operacje na istniejących encjach użytkownika.
 */
@Path("user")
@RequestScoped
@Api(value = "User Endpoint")
@Interceptors({AbstractAppExceptionEndpointInterceptor.class, EndpointLoggerInterceptor.class})
@TransactionAttribute(TransactionAttributeType.NEVER)
public class UserEndpoint extends AbstractEndpoint {

    @Inject
    UserManagerLocal userManager;

    @Override
    protected ManagerLocal getManager() {
        return userManager;
    }

    @GET
    @Produces(APPLICATION_JSON)
    @Path("self")
    public Response getSelfUser() throws AbstractAppException {
        try {
            UserWithPersonalDataAccessLevelDTO user = UserConverter.userWithPersonalDataAccessLevelDTOfromEntity(
                    retrying(userManager::getSelfUser)
            );
            return Response.ok(user).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("logChangeAccessLevel")
    public Response logAccessLevelChange(@Valid AccessLevelLogDTO accessLevelLogDTO) throws AbstractAppException {

        try {
            userManager.logAccessLevelChange(accessLevelLogDTO.getLogin(), accessLevelLogDTO.getAccessLevel());

            return Response.ok().build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa zwracjąca listę użytkowników razem z informacjami o danym koncie
     *
     * @return 200: body: wszyscy użytkownicy
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public Response getAllUsers() throws AbstractAppException {
        try {
            List<UserWithAccessLevelDTO> userWithAccessLevelDTOS = UserConverter.userWithAccessLevelDTOListFromEntities(
                    retrying(userManager::getAllUsers)
            );
            return Response.ok(userWithAccessLevelDTOS).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda rest umożliwiająca edycję danych personalnych użytkownika
     *
     * @param newData nowe dane personalne użytkownika
     * @param id      identyfikator użytkownika
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * 412: encja została zedytowana lub usunięta w trakcie wykonywania
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@NotNull @Valid PersonalDataDTO newData, @PathParam("id") long id) throws AbstractAppException {
        try {
            UserWithPersonalDataDTO userWithPersonalDataDTO = UserConverter.userWithPersonalDataFromEntity(
                    retrying(() ->
                            userManager.editUserData(PersonalDataConverter.personalDataEntityFromDTO(newData, id))
                    )
            );
            return Response
                    .status(Response.Status.OK)
                    .entity(userWithPersonalDataDTO)
                    .build();
        } catch (NotAllowedAppException | EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa wysyłająca wiadomość email z tokenem pozwalającym na zmiane adresu email.
     *
     * @param login - login uzytkownika
     * @param email - adres email przypisany do konta uzytkownika
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @GET
    @Path("{login}/requestChangeEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestChangeEmail(@PathParam("login") String login, @QueryParam("email") String email) throws AbstractAppException {
        try {
            UserWithAccessLevelDTO userWithAccessLevelDTO = UserConverter.userWithAccessLevelDTOFromEntity(
                    retrying(() -> userManager.requestChangeEmail(login, email))
            );
            return Response
                    .status(Response.Status.OK)
                    .entity(userWithAccessLevelDTO)
                    .build();
        } catch (NotAllowedAppException | EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa zmieniająca adres email konta użytkownika.
     *
     * @param id - identyfikator uzytkownika
     * @param token - jednorazowy token uzytkownika, ważny 20 minut
     * @param email - adres email przypisany do konta uzytkownika
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @GET
    @Path("changeEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeEmail(@QueryParam("id") long id, @QueryParam("token") String token, @QueryParam("email") @NotNull String email)
            throws AbstractAppException {
        try {
            UserWithAccessLevelDTO userWithAccessLevelDTO = UserConverter.userWithAccessLevelDTOFromEntity(
                    retrying(() -> userManager.changeEmail(id, token, email))
            );
            return Response
                    .status(Response.Status.OK)
                    .entity(userWithAccessLevelDTO)
                    .build();
        } catch (NotAllowedAppException | EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa zmieniająca hasło konta użytkownika.
     *
     * @param passwordChangeDTO - obiekt encji DTO zawierający stare i nowe hasło
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@Valid PasswordChangeDTO passwordChangeDTO) throws AbstractAppException {
        try {
            return Response
                    .status(Response.Status.OK)
                    .entity(
                            retrying(() -> UserConverter.userWithAccessLevelDTOFromEntity(userManager.changePassword(
                                    passwordChangeDTO.getOldPassword(),
                                    passwordChangeDTO.getNewPassword())
                            ))
                    )
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa pozwalająca na zweryfikowanie użytkownka w systemie za pomocą linku wysylanego w wiadomosći email.
     *
     * @param id - identyfikator uzytkownika
     * @param token - jednorazowy token uzytkownika, ważny 20 minut
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyUser(@QueryParam("id") long id, @QueryParam("token") String token) throws AbstractAppException {
        try {
            UserWithAccessLevelDTO activatedUser = UserConverter.userWithAccessLevelDTOFromEntity(
                    retrying(() -> userManager.verifyUser(id, token))
            );
            return Response
                    .status(Response.Status.OK)
                    .entity(activatedUser)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa wysyłająca wiadomość email z tokenem pozwalającym na zresetowanie hasła.
     *
     * @param email - adres email przypisany do konta uzytkownika
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @GET
    @Path("requestResetPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestResetPassword(@QueryParam("email") String email) throws AbstractAppException {
        UserWithAccessLevelDTO userWithAccessLevelDTO = UserConverter.userWithAccessLevelDTOFromEntity(
                retrying(() -> userManager.requestResetPassword(email))
        );
        return Response
                .status(Response.Status.OK)
                .entity(userWithAccessLevelDTO)
                .build();
    }

    /**
     * Metoda endpointowa resetująca hasło konta użytkownika.
     *
     * @param id - identyfikator uzytkownika
     * @param token - jednorazowy token uzytkownika, ważny 20 minut
     * @param passwordChangeDTO - obiekt encji DTO zawierający stare i nowe hasło
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("resetPassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPassword(@QueryParam("id") long id, @QueryParam("token") String token, @Valid @NotNull PasswordDTO passwordChangeDTO) throws AbstractAppException {
        try {
            UserWithAccessLevelDTO userWithAccessLevelDTO = UserConverter.userWithAccessLevelDTOFromEntity(
                    retrying(() -> userManager.resetPassword(id, token, passwordChangeDTO.getPassword()))
            );
            return Response
                    .status(Response.Status.OK)
                    .entity(userWithAccessLevelDTO)
                    .build();
        } catch (NotAllowedAppException | EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }


    /**
     * Metoda endpointowa aktywująca konto użytkownika.
     *
     * @param id - identyfikator użytkownika
     * @return 202: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("{id}/activate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@PathParam("id") Long id) throws AbstractAppException {
        try {
            UserWithAccessLevelDTO activatedUser = UserConverter.userWithAccessLevelDTOFromEntity(
                    retrying(() -> userManager.activateUserAccount(id))
            );
            return Response
                    .status(Response.Status.ACCEPTED)
                    .entity(activatedUser)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa deaktywująca konto użytkownika.
     *
     * @param id - indentyfikator użytkownika
     * @return 202: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * 404: status zwracany w momencie nie znalezienia uzytkownika
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("{id}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateUser(@PathParam("id") Long id) throws AbstractAppException {
        try {
            UserWithAccessLevelDTO deactivatedUser = UserConverter.userWithAccessLevelDTOFromEntity(
                    retrying(() -> userManager.deactivateUserAccount(id))
            );
            return Response
                    .status(Response.Status.ACCEPTED)
                    .entity(deactivatedUser)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda pozwala na wyszukiwanie użytkowników systemu podając frazę zawierającą część ich imienia lub nazwiska
     *
     * @param query - fraza zawierająca część imienia lub nazwiska
     * @return userWithAccessLevelDTOS - lista znalezionych kont uzytkowników
     * @throws AbstractAppException - wyjątki zadeklarowane w interceptorze
     */
    @GET
    @Path("getUsersByPieceOfPersonalData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPieceOfEmail(@QueryParam("query") String query) throws AbstractAppException {
        try {
            List<UserWithAccessLevelDTO> userWithAccessLevelDTOS = UserConverter.userWithAccessLevelDTOListFromEntities(
                    retrying(() -> userManager.getUserByPieceOfData(query))
            );
            return Response.ok(userWithAccessLevelDTOS).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * metoda endpointowa zwracająca informacje o koncie użytkownika
     *
     * @param id - identyfikator użytkownika
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * 404: status zwracany w momencie nie znalezienia uzytkownika
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClient(@PathParam("id") Long id) {
        try {
            UserEntity userEntity = retrying(() -> userManager.getUser(id));
            return Response.ok(UserConverter.userWithAccessLevelDTOFromEntity(userEntity)).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        } catch (AbstractAppException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        }
    }

    /**
     * metoda endpointowa zwracająca listę użytkowników
     *
     * @param page - numer strony
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 202: status zwracany gdy nie zostanie znaleziony zaden uzytkownik
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @GET
    @Path("all/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClients(@PathParam("page") Long page) throws AbstractAppException {
        try {
            List<UserEntity> userEntities = retrying(() -> userManager.getUsersByPage(page));
            if (userEntities.isEmpty()) {
                return Response.noContent().build();
            } else {
                return Response.ok(UserConverter.userWithAccessLevelDTOListFromEntities(userEntities)).build();
            }
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }


    @GET
    @Path("failedLogin/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastFailedLogin(@PathParam("id") Long id) throws AbstractAppException {

        try {
            SessionLogEntity sessionLogEntity = retrying(() -> userManager.getLastFailedLogin(id));
            return Response.ok(SessionLogConverter.sessionLogDTOFromEntity(sessionLogEntity)).build();
        } catch (SessionLogNotFoundException e) {
            return Response
                    .status(Response.Status.NO_CONTENT)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    @GET
    @Path("successLogin/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastSuccessfulLogin(@PathParam("id") Long id) throws AbstractAppException {

        try {
            SessionLogEntity sessionLogEntity = retrying(() -> userManager.getLastSuccessfulLogin(id));
            return Response.ok(SessionLogConverter.sessionLogDTOFromEntity(sessionLogEntity)).build();
        } catch (SessionLogNotFoundException e) {
            return Response
                    .status(Response.Status.NO_CONTENT)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    @GET
    @Path("failedOwnLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnLastFailedLogin() throws AbstractAppException {

        try {
            SessionLogEntity sessionLogEntity = retrying(() -> userManager.getOwnLastFailedLogin());
            return Response.ok(SessionLogConverter.sessionLogDTOFromEntity(sessionLogEntity)).build();
        } catch (SessionLogNotFoundException e) {
            return Response
                    .status(Response.Status.NO_CONTENT)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    @GET
    @Path("successOwnLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnLastSuccessfulLogin() throws AbstractAppException {

        try {
            SessionLogEntity sessionLogEntity = retrying(() -> userManager.getOwnLastSuccessfulLogin());
            return Response.ok(SessionLogConverter.sessionLogDTOFromEntity(sessionLogEntity)).build();
        } catch (SessionLogNotFoundException e) {
            return Response
                    .status(Response.Status.NO_CONTENT)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }


    /**
     * metoda endpointowa pozwalająca na zmianę poziomów dostępów użytkownika
     *
     * @param id                 - identyfikator użytkownika
     * @param accessLevelDTOList - lista nowych poziomów dostępów
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @PUT
    @Path("change-privileges/{id}")
    @Consumes(APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePrivileges(@PathParam("id") Long id, List<AccessLevelDTO> accessLevelDTOList)
            throws AbstractAppException {
        try {
            UserEntity userEntity = retrying(() -> userManager.changePrivileges(id, accessLevelDTOList));
            return Response.ok(UserConverter.userWithAccessLevelDTOFromEntity(userEntity)).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * metoda endpointowa pozwalająca na usunięcie użytkownika
     *
     * @param id - identyfikator użytkownika
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * 404: status zwracany w momencie braku uzytkownika o podanym id
     * 412: status zwracany w momencie naruszenia zasad struktury danych
     * @throws AbstractAppException abstrakcyjny wyjątek aplikacyjny
     */
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") Long id) throws AbstractAppException {

        try {
            retrying(() -> {
                userManager.deleteUser(id);
                return null;
            });
            return Response.noContent().status(Response.Status.OK).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa pozwalająca na zmiane statusu poziomu dostepu o zadanym id na zadany status
     * @param id identyfikator poziomu dostepu
     * @param status Nowy status dla pozuiomu dostepu o zadanym id
     * @return 200: dane zmienione poprawnie, body: aktualny stan encji
     */
    @PUT
    @Path("accessLevelChangeStatus/{id}/{status}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(EndpointLoggerInterceptor.class)
    public Response changeEntertainerAccessLevelStatus(@PathParam("id") Long id, @PathParam("status") boolean status) throws AbstractAppException {
        AccessLevelEntity accessLevel = null;
        try {
            accessLevel = retrying(() -> userManager.changeAccessLevelStatus(id, status));
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
}
