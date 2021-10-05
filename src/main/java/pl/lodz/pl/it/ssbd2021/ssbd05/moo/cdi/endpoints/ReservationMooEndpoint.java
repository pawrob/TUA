package pl.lodz.pl.it.ssbd2021.ssbd05.moo.cdi.endpoints;

import io.swagger.annotations.Api;
import pl.lodz.pl.it.ssbd2021.ssbd05.cdi.endpoints.AbstractEndpoint;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.RatingDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ReservationDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ReservationEditDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ReservationEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.NotAllowedAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.AbstractAppExceptionEndpointInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers.ReservationManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.ReservationConverter;

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
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Api(value = "Reservation MOO Endpoint")
@TransactionAttribute(TransactionAttributeType.NEVER)
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("/moo/reservation")
@RequestScoped
@Interceptors(AbstractAppExceptionEndpointInterceptor.class)
public class ReservationMooEndpoint extends AbstractEndpoint {

    @Inject
    private ReservationManagerLocal reservationManager;


    @Override
    protected ManagerLocal getManager() {
        return reservationManager;
    }
    /**
     * metoda endpointowa zwracjąca rezerwacje
     *
     * @return 200: body: wszystkie rezerwacje
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @GET
    @Path("/all")
    public Response list() throws AbstractAppException {
        try {
            List<ReservationEntity> reservations = retrying(() -> reservationManager.getAllReservations());
            return Response
                    .ok()
                    .entity(ReservationConverter.createReservationListDTOFromEntity(reservations))
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * metoda endpointowa zwracjąca rezerwacje entertainera
     *
     * @return 200: body: wszystkie rezerwacje
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @GET
    @Path("/allForEntertainer")
    public Response listForEntertainer() throws AbstractAppException {
        try {
            List<ReservationEntity> reservations = retrying(() -> reservationManager.getAllReservationsForEntertainer());
            return Response
                    .ok()
                    .entity(ReservationConverter.createReservationListDTOFromEntity(reservations))
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * metoda endpointowa zwracjąca rezerwacje zalogowanego użytkownika
     *
     * @return 200: body: wszystkie rezerwacje
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @GET
    @Path("/self")
    public Response listSelf() throws AbstractAppException {
        try {
            List<ReservationEntity> reservationEntities = retrying(() -> reservationManager.getSelfReservations());
            return Response.ok(ReservationConverter.createReservationListDTOFromEntity(reservationEntities)).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa pozwalająca na stworzenie rezerwacji
     * @param reservation obiekt DTO zawierajacy dane o zakladanej rezerwacji
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @POST
    @Consumes(APPLICATION_JSON)
    public Response create(@NotNull ReservationDTO reservation) throws AbstractAppException {
        ReservationEntity reservationEntity = ReservationConverter.createEntityFromDto(reservation);
        try {
            ReservationEntity createdReservation = retrying (() -> reservationManager.createReservation(reservationEntity, reservation.getOffer(), reservation.getOfferVersion()));
            return Response
                    .ok()
                    .entity(ReservationConverter.reservationEntityToDTO(createdReservation))
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }

    }
    /**
     * metoda endpointowa zwracjąca rezerwacje o wskazanym id
     *
     *
     * @param id - identyfikator rezerwacji, ktorej opis ma byc pobrany
     * @return 200: body: rezerwacja
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        try {
            var reservation = retrying(() -> reservationManager.getReservation(id));
            return Response.ok(ReservationConverter.reservationEntityToDTO(reservation)).build();
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
     * metoda endpointowa pozwalająca na edycje rezerwacji
     *
     *
     * @param id - identyfikator rezerwacji, która ma byc edytowana
     * @return 200: body: wszystkie rezerwacje
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @PUT
    @Path("/{id}")
    public Response edit(@PathParam("id") Long id, @Valid ReservationEditDTO reservationEditDTO) {
        try {
            var reservation = retrying(() -> reservationManager.editReservation(id, reservationEditDTO));
            return Response
                    .ok()
                    .entity(ReservationConverter.reservationEntityToDTO(reservation))
                    .build();
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
     * metoda endpointowa pozwalająca na anulowanie rezerwacji przez klienta
     *
     * @param id - identyfikator rezerwacji, która ma byc anulowana
     * @return 200: body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @PUT
    @Path("/{id}/client-cancel")
    public Response clientCancel(@PathParam("id") Long id) throws AbstractAppException {
        try {
            var reservation = retrying(() -> reservationManager.clientCancelReservation(id));
            return Response.ok(ReservationConverter.reservationEntityToDTO(reservation)).build();
        } catch (NotAllowedAppException | EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * metoda endpointowa pozwalająca na anulowanie rezerwacji przez entertainera
     *
     * @param id - identyfikator rezerwacji, która ma byc anulowana
     * @return 200: body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @PUT
    @Path("/{id}/entertainer-cancel")
    public Response entertainerCancel(@PathParam("id") Long id) throws AbstractAppException {
        try {
            var reservation = retrying(() -> reservationManager.entertainerCancelReservation(id));
            return Response.ok(ReservationConverter.reservationEntityToDTO(reservation)).build();
        } catch (NotAllowedAppException | EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
    /**
     *
     *
     * @param id
     * @return
     * @throws AbstractAppException
     */
    //ENTERTAINER
    @PUT
    @Path("/{id}/end")
    public Response markAsEnded(@PathParam("id") Long id) throws AbstractAppException {
        try {
            ReservationEntity reservation = retrying(() -> reservationManager.endReservation(id));
            return Response
                    .ok()
                    .entity(ReservationConverter.reservationEntityToDTO(reservation))
                    .build();
        } catch (EJBAccessException | AccessLocalException | NotAllowedAppException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
    /**
     * metoda endpointowa pozwalająca na akceptacje rezerwacji przez entertainera
     *
     * @param id - identyfikator rezerwacji, która ma byc akceptowana
     * @return 200: body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @PUT
    @Path("/{id}/accept")
    public Response accept(@PathParam("id") Long id) throws AbstractAppException {
        try {
            ReservationEntity reservation = retrying(() -> reservationManager.acceptReservation(id));
            return Response
                    .ok()
                    .entity(ReservationConverter.reservationEntityToDTO(reservation))
                    .build();
        } catch (EJBAccessException | AccessLocalException | NotAllowedAppException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }


    /**
     * Metoda endpointu pozwalająca na modyfikację oceny i komentarza do danej rezerwacji
     *
     * @param id - identyfikator rezerwacji
     * @param ratingDTO - DTO z oceną i komentarzem
     * @return 202: ocena i komentarz zostały zmodyfikowane, body - encja rezerwacji;   403: brak dostępu, body - opis bledu
     * @throws AbstractAppException
     */
    @PUT
    @Path("/{id}/rating")
    public Response updateRating(@PathParam("id") Long id, @Valid RatingDTO ratingDTO) throws AbstractAppException {
        try {
            ReservationDTO reservationDTO = ReservationConverter.reservationEntityToDTO(
                    retrying(() -> reservationManager.updateRating(id, ratingDTO.getRating(), ratingDTO.getComment()))
            );
            return Response
                    .status(Response.Status.ACCEPTED)
                    .entity(reservationDTO)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointu pozwalająca na usunięcie oceny i komentarza do danej rezerwacji przez użytkownika z poziomem dostępu Client
     *
     * @param id - identyfikator rezerwacji
     * @return 202: ocena i komentarz zostały usunięte, body - encja rezerwacji;   403: brak dostępu, body - opis bledu
     * @throws AbstractAppException
     */
    @DELETE
    @Path("/{id}/rating")
    public Response deleteRatingByClient(@PathParam("id") Long id) throws AbstractAppException {
        try {
            ReservationDTO reservationDTO = ReservationConverter.reservationEntityToDTO(
                    retrying(() -> reservationManager.deleteRatingByClient(id))
            );
            return Response
                    .status(Response.Status.ACCEPTED)
                    .entity(reservationDTO)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointu pozwalająca na usunięcie oceny i komentarza do danej rezerwacji przez użytkownika z poziomem dostępu Management
     *
     * @param id - identyfikator rezerwacji
     * @return 202: ocena i komentarz zostały usunięte, body - encja rezerwacji;   403: brak dostępu, body - opis bledu
     * @throws AbstractAppException
     */
    @DELETE
    @Path("/{id}/management/rating")
    public Response deleteRatingByManagement(@PathParam("id") Long id) throws AbstractAppException {
        try {
            ReservationDTO reservationDTO = ReservationConverter.reservationEntityToDTO(
                    retrying(() -> reservationManager.deleteRatingByManagement(id))
            );
            return Response
                    .status(Response.Status.ACCEPTED)
                    .entity(reservationDTO)
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
}
