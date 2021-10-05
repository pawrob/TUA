package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferAvailabilityDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.OfferAvailabilityEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Klasa udostępniająca metody konwersji miedzy obiektami typu OfferAvailabilityEntity oraz OfferAvailabilityDTO
 */
public class OfferAvailabilityConverter {

    private OfferAvailabilityConverter() {
    }

    /**
     * Metoda pozwalająca na konwersję obiektu typu OfferAvailabilityEntity na obiekt typu OfferAvailabilityDTO
     * @param e
     * @return obiekt DTo reprezentujący wpis o dostępności oferty
     */
    public static OfferAvailabilityDTO offerAvailabilityEntityToDTO(OfferAvailabilityEntity e) {
        return new OfferAvailabilityDTO(e.getId(), e.getVersion(),
                e.getWeekDay(), e.getHoursFrom(), e.getHoursTo());
    }

    /**
     * Metoda pozwalająca na konwersję kolekcji obiektów typu OfferAvailabilityEntity na listę obiektów typu OfferAvailabilityDTO
     * @param entities
     * @return lista obiektów DTO reprezentujących wpisy o dostępności oferty
     */
    public static List<OfferAvailabilityDTO> createOfferAvailabilityListDTOFromEntity(Collection<OfferAvailabilityEntity> entities) {
        return null == entities ? null : entities.stream()
                .filter(Objects::nonNull)
                .map(OfferAvailabilityConverter::offerAvailabilityEntityToDTO)
                .collect(Collectors.toList());
    }
}
