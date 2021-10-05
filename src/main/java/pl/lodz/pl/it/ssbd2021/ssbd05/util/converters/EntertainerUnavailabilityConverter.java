package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerUnavailabilityDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerUnavailabilityEntireDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerUnavailabilityEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasa udostępniająca metody konwersji miedzy obiektami typu EntertainerUnavailability oraz EntertainerUnavailabilityDTO
 */

public class EntertainerUnavailabilityConverter {

    public static EntertainerUnavailabilityEntity createEntityFromDTO(EntertainerUnavailabilityDTO unavailability,
                                                                      boolean isValid, long entertainerId) {
        return new EntertainerUnavailabilityEntity(unavailability.getDateTimeFrom(),
                unavailability.getDateTimeTo(),
                unavailability.getDescription(),
                isValid,
                entertainerId);
    }

    public static EntertainerUnavailabilityEntity createEntireEntityFromDTO(EntertainerUnavailabilityEntireDTO unavailabilityDTO) {
        return new EntertainerUnavailabilityEntity(unavailabilityDTO.getDateTimeFrom(), unavailabilityDTO.getDateTimeTo(),
                unavailabilityDTO.getDescription(), unavailabilityDTO.isValid(), unavailabilityDTO.getEntertainerId());
    }


    /**
     * Metoda pozwalajaca na konwersję obiektu typu EntertainerUnavailabilityEntity na obiekt typu EntertainerUnavailabilityDTO
     * @param unavailability
     * @return obiekt DTO reprezentujący wpis o niedostępności entertainer'a
     */
    public static EntertainerUnavailabilityDTO toDto(EntertainerUnavailabilityEntity unavailability) {

        return new EntertainerUnavailabilityDTO(unavailability.getId(),
                unavailability.getVersion(),
                unavailability.getDateTimeFrom(),
                unavailability.getDateTimeTo(),
                unavailability.getDescription());
    }

    /**
     * Metoda pozwalajaca na konwersję listy obiektów typu EntertainerUnavailabilityEntity na liste obiektó typu EntertainerUnavailabilityDTO
     * @param unavailability
     * @return lista obiektó DTO reprezentujących wpis o niedostępności entertainer'a
     */
    public static List<EntertainerUnavailabilityDTO> toDtoList(List<EntertainerUnavailabilityEntity> unavailability) {
        return unavailability.stream().map(EntertainerUnavailabilityConverter::toDto).collect(Collectors.toList());
    }
}
