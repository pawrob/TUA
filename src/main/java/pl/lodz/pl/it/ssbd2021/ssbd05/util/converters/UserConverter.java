package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.UserDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.UserWithAccessLevelDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.UserWithPersonalDataAccessLevelDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.UserWithPersonalDataDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Klasa odpowiadająca za konwertowanie obiektów encji i dto uzytkownika.
 */
public class UserConverter {//needs version hashing somewhere

    private UserConverter() {
    }

    /**
     * Konwerter obiektu encji uzytkownika na obiekt DTO.
     *
     * @param userEntity - obiekt encji uzytkownika
     * @return
     */
    public static UserDTO userEntityToDTO(UserEntity userEntity) {
        return new UserDTO(userEntity.getId(), userEntity.getVersion(),
                userEntity.getLogin(), userEntity.getEmail(), userEntity.isActive(),
                userEntity.isActive());
    }

    /**
     * Konwerter obiektu UserDTO oraz hasła na obiekt encji uzytkownika.
     *
     * @param userDTO  - obiekt DTO uzytkownika
     * @param password - hasło uzytkownika
     * @return obiekt encji uzytkownika
     */
    public static UserEntity createNewUserEntityFromDTO(UserDTO userDTO, String password) {
        return new UserEntity(userDTO.getLogin(), userDTO.getEmail(), password);
    }

    /**
     * Konwerter obiektu UserWithPersonalDataDTO oraz hasła na obiekt encji uzytkownika.
     *
     * @param userDTO  - obiekt DTO uzytkownika
     * @param password - hasło uzytkownika
     * @return obiekt encji uzytkownika
     */
    public static UserEntity userWithPersonalDataDTOtoEntity(UserWithPersonalDataDTO userDTO, String password) {
        UserEntity userEntity = createNewUserEntityFromDTO(userDTO, password);
        userEntity.setActive(userDTO.isActive());
        userEntity.setVerified(userDTO.isVerified());
        if (null != userDTO.getPersonalData()) {
            userEntity.setPersonalData(
                    PersonalDataConverter.personalDataEntityFromDTO(
                            userDTO.getPersonalData(), userDTO.getId()
                    ));
        }
        return userEntity;
    }

    /**
     * Konwerter listy obiektow encji uzytkownikow na listę obiektów DTO uzytkownika.
     *
     * @param userEntities - lista obiektow encji uzytkownikow
     * @return lista obiektow DTO uzytkownikow
     */
    public static List<UserDTO> createUserListDTOFromEntity(Collection<UserEntity> userEntities) {
        return null == userEntities ? null : userEntities.stream()
                .filter(Objects::nonNull)
                .map(UserConverter::userEntityToDTO)
                .collect(Collectors.toList());
    }


    /**
     * Konwerter obiektu Encji na obiekt DTO uzytkownika z poziomami dostepu.
     *
     * @param userEntity - obiekt encji uzytkownika
     * @return obiekt DTO uzytkownika z poziomami dostepu
     */
    public static UserWithAccessLevelDTO userWithAccessLevelDTOFromEntity(UserEntity userEntity) {
        return new UserWithAccessLevelDTO(userEntity.getId(), userEntity.getVersion(), userEntity.getLogin(),
                userEntity.getEmail(), userEntity.isActive(), userEntity.isVerified(),
                AccessLevelConverter.AccessLevelDTOListFromEntities(userEntity.getAccessLevels()));
    }

    /**
     * Konwerter listy obiektow encji uzytkownikow na listę obiektów DTO uzytkownika z poziomami dostepu.
     *
     * @param userEntities
     * @return lista obiektow DTO uzytkownikow z poziomami dostepu
     */
    public static List<UserWithAccessLevelDTO> userWithAccessLevelDTOListFromEntities(Collection<UserEntity> userEntities) {
        return null == userEntities ? null : userEntities.stream()
                .filter(Objects::nonNull)
                .map(UserConverter::userWithAccessLevelDTOFromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Konwerter obiektu Encji na obiekt DTO uzytkownika z danymi personalnymi.
     *
     * @param userEntity - obiekt encji uzytkownika
     * @return obiekt DTO uzytkownika z danymi personalnymi.
     */
    public static UserWithPersonalDataDTO userWithPersonalDataFromEntity(UserEntity userEntity) {
        return new UserWithPersonalDataDTO.Builder()
                .id(userEntity.getId())
                .version(userEntity.getVersion())
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .isActive(userEntity.isActive())
                .isVerified(userEntity.isVerified())
                .personalData(PersonalDataConverter.personalDataDTOfromEntity(userEntity.getPersonalData()))
                .build();
    }

    /**
     * Konwerter obiektu Encji na obiekt DTO uzytkownika z danymi personalnymi oraz poziomami dostępu.
     *
     * @param userEntity
     * @return obiekt DTO uzytkownika z danymi personalnymi oraz poziomami dostępu
     */
    public static UserWithPersonalDataAccessLevelDTO userWithPersonalDataAccessLevelDTOfromEntity(UserEntity userEntity) {
        return new UserWithPersonalDataAccessLevelDTO(
                userEntity.getLogin(),
                userEntity.getEmail(),
                userEntity.isActive(),
                userEntity.isVerified(),
                AccessLevelConverter.AccessLevelDTOListFromEntities(userEntity.getAccessLevels()),
                PersonalDataConverter.personalDataDTOfromEntity(userEntity.getPersonalData()));

//        return UserWithPersonalDataAccessLevelDTO.builder()
//                .personalData(PersonalDataConverter.personalDataDTOfromEntity(userEntity.getPersonalData()))
//                .id(userEntity.getId())
//                .version(userEntity.getVersion())
//                .login(userEntity.getLogin())
//                .email(userEntity.getEmail())
//                .isActive(userEntity.isActive())
//                .isVerified(userEntity.isVerified())
//                .accessLevels(AccessLevelConverter.AccessLevelDTOListFromEntities(
//                        userEntity.getAccessLevels()))
//                .build();
    }
}
