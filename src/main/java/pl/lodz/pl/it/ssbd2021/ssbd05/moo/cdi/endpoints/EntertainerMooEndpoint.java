package pl.lodz.pl.it.ssbd2021.ssbd05.moo.cdi.endpoints;

import io.swagger.annotations.Api;
import pl.lodz.pl.it.ssbd2021.ssbd05.cdi.endpoints.AbstractEndpoint;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.DescriptionDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerUnavailabilityDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerUnavailabilityEntireDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerUnavailabilityEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.AbstractAppExceptionEndpointInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers.EntertainerMooManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.EntertainerConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.EntertainerUnavailabilityConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EndpointLoggerInterceptor;

import javax.annotation.security.PermitAll;
import javax.ejb.AccessLocalException;
import javax.ejb.EJBAccessException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Endpoint CDI odpowiadający za operacje które mogą być wykonywane przez entertainera".
 */
@Api(value = "Entertainer MOO Endpoint")
@TransactionAttribute(TransactionAttributeType.NEVER)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/moo/entertainer")
@RequestScoped
@Interceptors({AbstractAppExceptionEndpointInterceptor.class, EndpointLoggerInterceptor.class})
public class EntertainerMooEndpoint extends AbstractEndpoint {

    @Inject
    EntertainerMooManagerLocal entertainerManager;

    @Override
    protected ManagerLocal getManager() {
        return entertainerManager;
    }

    /**
     * Metoda endpointu pozwalająca na pobieranie opis uwskazanego entertainera
     *
     * @param id - identyfikator konta, ktorego opis ma byc pobrany
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @GET
    @Path("/getDescription/{id}/")
    public Response getDescription(@PathParam("id") Long id) throws AbstractAppException {
        try {
            String description = retrying(() -> entertainerManager.getDescription(id));
            return Response.ok(description).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
    /**
     * Metoda endpointu pozwalająca na pobieranie opis uwierzytelnionego uzytkownika
     *
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @GET
    @Path("/description/")
    public Response getSelfDescription() throws AbstractAppException {
        try {
            String description = retrying(() -> entertainerManager.getSelfDescription());
            return Response.ok(description).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointu pozwalająca na edycje opis uwierzytelnionego uzytkownika
     *
     * @param description - obiekt DTO zawierający edytowany opis
     * @return 200: dane zaaktualizowane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @PUT
    @Path("/description/")
    public Response updateDescription(DescriptionDTO description) throws AbstractAppException {
        try {
            String resultDescription = retrying(() -> entertainerManager.updateDescription(description.getDescription()));
            return Response.ok(resultDescription).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
    /**
     * Metoda endpointu pozwalająca na pobieranie podstawowych danych wskazanego entertainera
     *
     * @param id - identyfikator konta, ktorego opis ma byc pobrany
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @GET
    @Path("/basicEntertainerInfo/{id}")
    public Response getBasicEntertainerInfo(@PathParam("id") Long id) throws AbstractAppException {
        try {
            EntertainerDTO entertainerDTO = retrying(() -> EntertainerConverter.convertToBasicDTO(entertainerManager.getBasicEntertainerInfo(id)));
            return Response.ok(entertainerDTO).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointu pozwalająca na pobieranie niedostepnosci wskazanego entertainera
     *
     * @param id - identyfikator konta, ktorego dane mają byc pobrane
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @GET
    @Path("/unavailability/{id}")
    public Response getUnavailability(@PathParam("id")Long id) {
        try {
            return Response
                    .ok()
                    .entity(EntertainerUnavailabilityConverter.toDtoList(
                            entertainerManager.getUnavailability(id)))
                    .build();
        } catch (AbstractAppException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    //ENTERTAINER
    @PUT
    @Path("/unavailability/{id}/{status}")
    public Response updateUnavailabilityStatus(@PathParam("id") long id, @PathParam("status") boolean status) throws AbstractAppException {
        EntertainerUnavailabilityEntity result = retrying(() -> entertainerManager.updateUnavailabilityStatus(id, status));
        return Response.ok()
                .entity(result)
                .build();
    }

    @GET
    @PermitAll
    @Path("/unavailability")
    public Response getUnavailability() throws AbstractAppException {
        return Response.ok()
                .entity(entertainerManager.getUnavailabilities())
                .build();
    }

    @GET
    @Path("/unavailability/my")
    public Response getMyUnavailability() throws AbstractAppException {
        List<EntertainerUnavailabilityEntity> unavailabilities = entertainerManager.getMyUnavailabilities();
        return Response.ok()
                .entity(unavailabilities)
                .build();
    }

    /**
     * Metoda endpointu pozwalająca na edycje niedostepnosci wskazanego entertainera
     *
     * @return 200: dane zaaktualizowane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @PUT
    @Consumes(APPLICATION_JSON)
    @Path("/unavailability")
    public Response updateUnavailability(@NotNull EntertainerUnavailabilityEntireDTO unavailabilityEntireDTO) throws AbstractAppException {
        try {
            EntertainerUnavailabilityEntity entity = retrying(() -> entertainerManager.updateUnavailability(unavailabilityEntireDTO));
            return Response
                    .accepted()
                    .entity(entity)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * MOO 18
     *
     * @param unavailability
     * @return
     */
    //ENTERTAINER
    @POST
    @Consumes(APPLICATION_JSON)
    @Path("/unavailability")
    public Response addUnavailability(@NotNull EntertainerUnavailabilityDTO unavailability) throws AbstractAppException {
        try {
            EntertainerUnavailabilityEntity entertainer = retrying(() -> entertainerManager.createUnavailability(unavailability));
            return Response.ok()
                    .entity(entertainer)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }

    }

}
