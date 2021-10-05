package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferToCreateDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferWithDetailsDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.OfferEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Klasa konwertera udostępniająca metody pozwalające na konwersję pomiedzy obiektami typu OfferEntity oraz OfferDTO
 */
public class OfferConverter {


    private OfferConverter() {
    }

    /**
     * Metoda pozwalająca na konwersję obiektu typu OfferEntity na obiekt typu OfferDTO
     * @param entity
     * @return obiekt DTO reprezentujący oferte
     */
    public static OfferDTO offerEntityToDTO(OfferEntity entity) {
        return new OfferDTO(entity.getId(), entity.getVersion(),
                entity.getTitle(), entity.getDescription(), entity.isActive(),
                entity.getValidFrom(), entity.getValidTo(), entity.getAvgRating());
    }

    /**
     * Metoda pozwalająca na konwersję obiektu typu OfferEntity na obiekt typu OfferDTO wraz z informacją czy jest ona dodana do listy ulubionych danego użytkownika
     * @param entity
     * @param isFavourite
     * @return obieekt DTo oferty z informacją o tym, czy jest w liście ulubionych
     */
    public static OfferDTO offerEntityToDTOWithFavourite(OfferEntity entity, boolean isFavourite) {
        return new OfferDTO(entity.getId(), entity.getVersion(),
                entity.getTitle(), entity.getDescription(), entity.isActive(),
                entity.getValidFrom(), entity.getValidTo(), entity.getAvgRating(), isFavourite);
    }

    /**
     * Metoda pozwalająca na konwersję kolekcji obiektów typu OfferEntity na liste obiektów typu OfferDTO
     * @param entities
     * @return lista obiektów DTo reprezentujących kolekcję offert
     */
    public static List<OfferDTO> createOfferListDTOFromEntity(Collection<OfferEntity> entities) {
        return null == entities ? null : entities.stream()
                .filter(Objects::nonNull)
                .map(OfferConverter::offerEntityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Metoda konwertująca mapę obiektów typu OfferEntity oraz iformacji o przynależności do listy ulubionych użytkownika
     * na listę obiektów typu OfferDTO
     * @param entities
     * @return lista obiektów DTO reprezentujących kolekcję offert z informacją o tym, czy są w liście ulubionych
     */
    public static List<OfferDTO> createOfferListDTOFromEntityWithFavourite(Map<OfferEntity, Boolean> entities) {
        return null == entities ? null : entities.entrySet().stream()
                .filter(Objects::nonNull)
                .map(o -> offerEntityToDTOWithFavourite(o.getKey(), o.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Metoda konwertująca obiekty typu OfferEntity na obiekty typu OfferWithDetailsDTO
     * @param e
     * @return obiekt typu DTo zawierający onformacje o ofercie wraz z jej opisem
     */
    public static OfferWithDetailsDTO offerEntityWithDetailsToDTO(OfferEntity e) {
        return new OfferWithDetailsDTO(e.getId(), e.getVersion(),
                e.getTitle(), e.getDescription(), e.isActive(),
                e.getValidFrom(), e.getValidTo(), e.getAvgRating(), ClientConverter.convertToListDTO(e.getLikedBy()),
                EntertainerConverter.convertToDTO(e.getEntertainer()),
                OfferAvailabilityConverter.createOfferAvailabilityListDTOFromEntity(e.getOfferAvailabilities()),
                ReservationConverter.createReservationListDTOFromEntity(e.getReservations()));
    }

    /**
     * Metoda konwertująca kolekcję obiektów typu OfferEntity  na listę obiektów typu OfferWithDetailsDTO
     * @param entities
     * @return lista obiektów DTO reprezentujących kolekcję offert z ich opisami
     */
    public static List<OfferWithDetailsDTO> createOfferListWithDetailsDTOFromEntity(Collection<OfferEntity> entities) {
        return null == entities ? null : entities.stream()
                .filter(Objects::nonNull)
                .map(OfferConverter::offerEntityWithDetailsToDTO)
                .collect(Collectors.toList());
    }

    public static OfferEntity createNewOfferEntityWithDetailsFromDTO(OfferToCreateDTO offer, EntertainerEntity entertainer) {
        return new OfferEntity(offer.getTitle(), offer.getDescription(), offer.isActive(),
                offer.getValidFrom(), offer.getValidTo(), entertainer);
    }


}
