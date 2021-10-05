package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.PersonalDataDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.PersonalDataEntity;

/**
 * Klasa konwertera konwertująca encje i DTO danych personalnych.
 */
public class PersonalDataConverter {

    private PersonalDataConverter() {

    }

    /**
     * Konwerter obiektu dto danych personalnych na obiekt encji danych personalnych.
     *
     * @param personalDataDTO - obiekt DTO danych personanych
     * @param userId          - id uzytkownika powiazanego z przekazanymi danymi personalnymi
     * @return encja obiektu danych personalnych
     */
    public static PersonalDataEntity personalDataEntityFromDTO(PersonalDataDTO personalDataDTO, long userId) {
        return new PersonalDataEntity(userId, personalDataDTO.getName(),
                personalDataDTO.getSurname(), personalDataDTO.getPhoneNumber(),
                personalDataDTO.getLanguage(), personalDataDTO.getIsMan(),
                personalDataDTO.getVersion());
    }

    /**
     * Konwerter obiektu encji danych personalnych na obiekt dto danych personalnych.
     *
     * @param personalDataEntity encja obiektu danych personalnych
     * @return obiekt DTO z danymi personalnymi
     */
    public static PersonalDataDTO personalDataDTOfromEntity(PersonalDataEntity personalDataEntity){
        return new PersonalDataDTO.Builder()
                .id(personalDataEntity.getUserId())
                .version(personalDataEntity.getVersion())
                .name(personalDataEntity.getName())
                .surname(personalDataEntity.getSurname())
                .phoneNumber(personalDataEntity.getPhoneNumber())
                .language(personalDataEntity.getLanguage())
                .isMan(personalDataEntity.getIsMan())
                .build();
    }

}
