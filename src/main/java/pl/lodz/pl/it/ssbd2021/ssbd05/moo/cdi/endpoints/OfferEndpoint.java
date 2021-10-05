package pl.lodz.pl.it.ssbd2021.ssbd05.moo.cdi.endpoints;

import io.swagger.annotations.Api;
import pl.lodz.pl.it.ssbd2021.ssbd05.cdi.endpoints.AbstractEndpoint;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferToCreateDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferEditDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferWithDetailsDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.OfferEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.interceptors.AbstractAppExceptionEndpointInterceptor;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers.OfferManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.converters.OfferConverter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EndpointLoggerInterceptor;

import javax.ejb.AccessLocalException;
import javax.ejb.EJBAccessException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "Offer MOO Endpoint")
@TransactionAttribute(TransactionAttributeType.NEVER)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/moo/offer")
@RequestScoped
@Interceptors({AbstractAppExceptionEndpointInterceptor.class, EndpointLoggerInterceptor.class})
public class OfferEndpoint extends AbstractEndpoint {

    @Inject
    private OfferManagerLocal offerManager;

    @Override
    protected ManagerLocal getManager() {
        return offerManager;
    }



    /**
     * metoda endpointowa zwracjąca oferty
     *
     * @return 200: body: wszystkie oferty
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @GET
    @Path("all")
    public Response getAllOffers() throws AbstractAppException {
        try {
            List<OfferDTO> offersDTO = OfferConverter.createOfferListDTOFromEntity(
                    retrying(offerManager::getAllOffers)
            );
            return Response.ok(offersDTO).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
    /**
     * metoda endpointowa zwracjąca oferty ze szczegółowymi danymi
     *
     * @return 200: body: wszystkie oferty
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @GET
    @Path("allWithDetails")
    public Response getAllOffersWithDetails() throws AbstractAppException {
        try {
            List<OfferWithDetailsDTO> offersWithDetailsDTO = OfferConverter.createOfferListWithDetailsDTOFromEntity(
                    retrying(offerManager::getAllOffers)
            );
            return Response.ok(offersWithDetailsDTO).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
    /**
     * metoda endpointowa zwracjąca aktywne oferty
     *
     * @return 200: body: wszystkie oferty
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @GET
    @Path("allActive")
    public Response getAllActiveOffersWithDetails() throws AbstractAppException {
        try {
            List<OfferDTO> offersWithDetailsDTO = OfferConverter.createOfferListDTOFromEntity(
                    retrying(offerManager::getAllActiveOffers)
            );
            return Response.ok(offersWithDetailsDTO).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }
    /**
     * metoda endpointowa zwracjąca oferty entertainera
     *
     * @return 200: body: wszystkie oferty
     * 403: status zwracany w momencie braku dostepu do zasobu
     * @throws AbstractAppException
     */
    @GET
    @Path("allEntertainer")
    public Response getAllEntertainerOffersWithDetails() throws AbstractAppException {
        try {
            List<OfferWithDetailsDTO> offersWithDetailsDTO = OfferConverter.createOfferListWithDetailsDTOFromEntity(
                    retrying(offerManager::getAllEntertainerOffers)
            );
            return Response.ok(offersWithDetailsDTO).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    //ENTERTAINER
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(OfferToCreateDTO offer) throws AbstractAppException {
        OfferEntity created;
        try {
            created = retrying(() -> offerManager.createOffer(offer));
            return Response
                    .ok()
                    .entity(OfferConverter.offerEntityToDTO(created))
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
//        throw new UnsupportedOperationException();
    }

    /**
     * Metoda endpointowa pozwalająca na pobranie informacji o konkretnej ofercie
     *
     * @param id - identyfikator oferty, która ma byc pobrana
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @GET
    @Path("/{id}")
    public Response getOffer(@PathParam("id") Long id) throws AbstractAppException {
        try {
            OfferEntity offerEntity = retrying(() -> offerManager.getOffer(id));
            return Response.ok(OfferConverter.offerEntityToDTO(offerEntity)).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa pozwalająca na pobranie informacji o konkretnej ofercie ze szczegółami
     *
     * @param id - identyfikator oferty, która ma byc pobrana
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @GET
    @Path("/withDetails/{id}")
    public Response getOfferWithDetails(@PathParam("id") Long id) throws AbstractAppException {
        try {
            OfferEntity offerEntity = retrying(() -> offerManager.getOffer(id));
            return Response.ok(OfferConverter.offerEntityWithDetailsToDTO(offerEntity)).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }


    /**
     *
     * @param id - identyfikator oferty, która ma byc edytowana
     * @param offerEditDTO - obiekt dto zawierajacy dane do zmiany
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: użytkownik o podanym id nie został odnaleziony
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @PUT
    @Path("/{id}")
    public Response edit(@PathParam("id") Long id, @Valid OfferEditDTO offerEditDTO) {
        try {
            var offerEntity = retrying(() -> offerManager.editOffer(id, offerEditDTO));
            return Response.ok(OfferConverter.offerEntityToDTO(offerEntity)).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        } catch (AbstractAppException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(e)
                    .build();
        }
    }

    //ENTERTAINER
    //TODO: złączyć z edit?
    @PUT
    @Path("/{id}/{active}")
    public Response activenessChange(@PathParam("id") Long id, @PathParam("active") boolean active) throws AbstractAppException {
        OfferEntity offer;
        try {
            offer = retrying(() -> offerManager.changeActiveOffer(id, active));
            return Response.ok()
                    .entity(OfferConverter.offerEntityToDTO(offer))
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa pozwalająca na deaktywacje oferty
     *
     * @param id - identyfikator oferty, która ma byc deaktywowana
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: oferta o podanym id nie została odnaleziona
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @PUT
    @Path("/{id}/deactivate")
    public Response deactivate(@PathParam("id") Long id) {
        try {
            var offerEntity = retrying(() -> offerManager.deactivateOffer(id));
            return Response.ok(OfferConverter.offerEntityToDTO(offerEntity)).build();
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
     * Metoda endpointowa pozwalająca na dodanie oferty do ulubionych
     *
     * @param id - identyfikator oferty, która ma byc dodana do ulubionych
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: oferta o podanym id nie została odnaleziona
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @POST
    @Path("/{id}/add-favorite")
    public Response addFavorite(@PathParam("id") Long id) throws AbstractAppException {
        try {
            OfferEntity offerEntity = retrying(() -> offerManager.addOfferToFavourites(id));
            return Response.ok(OfferConverter.offerEntityWithDetailsToDTO(offerEntity)).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa pozwalająca na usunięcie oferty z ulubionych
     *
     * @param id - identyfikator oferty, która ma byc usunięta z ulubionych
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 404: oferta o podanym id nie została odnaleziona
     * 400: błąd walidacji wprowadzonych danych
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @DELETE
    @Path("/{id}/delete-favorite")
    public Response deleteFavorite(@PathParam("id") Long id) throws AbstractAppException {
        try {
            OfferEntity offerEntity = retrying(() -> offerManager.deleteOfferFromFavourites(id));
            return Response
                    .status(Response.Status.ACCEPTED)
                    .entity(OfferConverter.offerEntityWithDetailsToDTO(offerEntity))
                    .build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }

    /**
     * Metoda endpointowa pozwalająca na pobranie ulubionych ofert
     *
     * @return 200: dane pobrane poprawnie, body: aktualny stan encji
     * 403: status zwracany w momencie braku dostepu
     * @throws AbstractAppException
     */
    @GET
    @Path("/favorites")
    public Response getFavouriteOffers() throws AbstractAppException {
        try {
            List<OfferEntity> offerEntity = retrying(() -> offerManager.getFavouriteOffers());
            return Response.ok(OfferConverter.createOfferListDTOFromEntity(offerEntity)).build();
        } catch (EJBAccessException | AccessLocalException e) {
            return Response.status(Response.Status.FORBIDDEN).entity("Access Denied").build();
        }
    }


}
