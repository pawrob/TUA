package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;


import pl.lodz.pl.it.ssbd2021.ssbd05.dto.AccessLevelDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Klasa konwertera konwertująca poziomy dostepu obirtków DTO i encji.
 */
public class AccessLevelConverter {
    public static AccessLevelDTO AccessLevelDTOFromEntity(AccessLevelEntity accessLevelEntity) {
        return new AccessLevelDTO(accessLevelEntity.getId(), accessLevelEntity.getVersion(), accessLevelEntity.getAccessLevel(), accessLevelEntity.isActive());
    }
    /**
     * Konwerter listy obiektów encji poziomów dostepu na liste obiektów DTO poziomów dostepu.
     *
     * @param accessLevelEntities lista obiektów encji poziomów dostepu
     * @return lista obiektów DTO poziomów dostepu
     */
    public static List<AccessLevelDTO> AccessLevelDTOListFromEntities(Collection<AccessLevelEntity> accessLevelEntities) {
        return null == accessLevelEntities ? null : accessLevelEntities.stream()
                .filter(Objects::nonNull)
                .map(AccessLevelConverter::AccessLevelDTOFromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Konwerter obiektu DTO poziomu dostepu oraz encji użytkownika na obiekt encji poziomu dostepu.
     *
     * @param accessLevelDTO - obiekt DTo poziomu dostepu
     * @param userEntity - obiekt encji uzytkownika
     * @return obiekt encji poziomu dostepu
     */
    public static AccessLevelEntity AccessLevelEntityFromDTO(AccessLevelDTO accessLevelDTO, UserEntity userEntity) {
        AccessLevelEntity result;
        switch (accessLevelDTO.getAccessLevel().toUpperCase()) {
            case "CLIENT":
                result = new ClientEntity(accessLevelDTO.isActive(), userEntity);
                break;
            case "ENTERTAINER":
                result = new EntertainerEntity(accessLevelDTO.isActive(), userEntity);
                break;
            case "MANAGEMENT":
                result = new ManagementEntity(accessLevelDTO.isActive(), userEntity);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + accessLevelDTO.getAccessLevel().toUpperCase());
        }

        return result;
    }

    /**
     * Metoda konwertująca kolekcję poziomów dostępów DTO oraz encji użytkownika na listę encji poziomu dostępów.
     *
     * @param accessLevelDTOS - kolekcja poziomów dostępów DTO
     * @param userEntity - obiekt encji użytkownika
     * @return lista encji poziomów dostępu
     */
    public static List<AccessLevelEntity> accessLevelEntityListFromDTOs(Collection<AccessLevelDTO> accessLevelDTOS, UserEntity userEntity) {
        return null == accessLevelDTOS ? null : accessLevelDTOS.stream()
                .filter(Objects::nonNull)
                .map(accessLevelDTO -> AccessLevelEntityFromDTO(accessLevelDTO, userEntity))
                .collect(Collectors.toList());
    }


}
