package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferEditDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.OfferEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Klasa konwertera udostępniająca metody konwersji pomiędzy OfferEditDTO oraz OfferEntity
 */
public class OfferEditConverter {


    private OfferEditConverter() {
    }

    /**
     * Metoda pozwalająca na konwersję obiektu typu OfferEntity na obiekt typu OfferEditDTO
     * @param entity
     * @return obiekt DTO reprezentujący oferte w raz z lista jej dostępności
     */
    public static OfferEditDTO offerEntityToDTO(OfferEntity entity) {
        var offer = new OfferEditDTO(entity.getId(), entity.getVersion(),
                entity.getTitle(), entity.getDescription(),
                entity.getValidFrom(), entity.getValidTo());
        offer.setOfferAvailabilities(OfferAvailabilityConverter.createOfferAvailabilityListDTOFromEntity(entity.getOfferAvailabilities()));
        return offer;
    }

    /**
     * Metoda pozwalająca na konwersję kolecji obiektów typu OfferEntity na listę obiektów OfferEditDTO
     * @param entities
     * @return lista obieków DTO reprezentujący oferty w raz z ich dostępnościami
     */
    public static List<OfferEditDTO> createOfferListDTOFromEntity(Collection<OfferEntity> entities) {
        return null == entities ? null : entities.stream()
                .filter(Objects::nonNull)
                .map(OfferEditConverter::offerEntityToDTO)
                .collect(Collectors.toList());
    }
}
