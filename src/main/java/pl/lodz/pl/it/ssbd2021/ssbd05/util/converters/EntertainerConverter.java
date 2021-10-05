package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Klasa konwertera konwertująca encje i DTO entertainera.
 */
public class EntertainerConverter {
    private EntertainerConverter() {

    }

    /**
     * konwerter encji entertainera do obiektu DTO.
     *
     * @param entertainerEntity - obiekt encji entertainera
     * @return - obiekt DTO entertainera
     */
    public static EntertainerDTO convertToDTO(EntertainerEntity entertainerEntity) {
        return new EntertainerDTO(
                entertainerEntity.getId(),
                entertainerEntity.getVersion(),
                entertainerEntity.getUser().getLogin(),
                entertainerEntity.getUser().getEmail(),
                entertainerEntity.getUser().isActive(),
                entertainerEntity.getUser().isVerified(),
                entertainerEntity.getDescription(),
                entertainerEntity.getAvgRating(),
                entertainerEntity.isActive()
        );
    }


    public static EntertainerDTO convertToBasicDTO(EntertainerEntity entertainerEntity) {
        return new EntertainerDTO(
                entertainerEntity.getUser().getLogin(),
                entertainerEntity.getUser().getEmail(),
                entertainerEntity.getDescription(),
                entertainerEntity.getAvgRating()
        );
    }

    /**
     * konwerter obiektu DTO entertainera na obiekt encji entertainera.
     *
     * @param entertainerDTO - obiekt DTO entertainera
     * @param password       - hasło entertainera
     * @return - obiekt encji entertainera
     */
    public static EntertainerEntity createNewEntertainerEntityFromDTO(EntertainerDTO entertainerDTO, String password) {
        UserEntity userEntity = new UserEntity(
                entertainerDTO.getLogin(), entertainerDTO.getEmail(), password);
        EntertainerEntity entertainerEntity = new EntertainerEntity();
        entertainerEntity.setActive(true); //Its default value
        entertainerEntity.setUser(userEntity);
        entertainerEntity.setDescription(entertainerDTO.getDescription());
        entertainerEntity.setAvgRating(entertainerDTO.getAvgRating());
        return entertainerEntity;
    }

    /**
     * konwerter obiektu dto entertainera i hasła do obiektu encji entertainera.
     *
     * @param entertainerDTO - obiekt dto entertainera
     * @param password       - hasło entertainera
     * @return obiekt encji entertainera
     */
    public static EntertainerEntity convertToEntity(EntertainerDTO entertainerDTO, String password) {
        UserEntity userEntity = new UserEntity(
                entertainerDTO.getLogin(), entertainerDTO.getEmail(), password);
        EntertainerEntity entertainerEntity = new EntertainerEntity();
        //todo set entity proper id?
        //entertainerEntity.setId(entertainerDTO.getId());
        entertainerEntity.setVersion(entertainerDTO.getVersion());
        entertainerEntity.setActive(true); //Its default value
        entertainerEntity.setUser(userEntity);
        entertainerEntity.setDescription(entertainerDTO.getDescription());
        entertainerEntity.setAvgRating(entertainerDTO.getAvgRating());
        return entertainerEntity;
    }

    /**
     * konwereter listy obiektów encji entertainerów do listy obiektów DTO entertainerów.
     *
     * @param entertainerEntityList - lista obiektów encji entertainerów
     * @return lista obiektów encji DTO entertainerów
     */
    public static List<EntertainerDTO> convertToListDTO(List<EntertainerEntity> entertainerEntityList) {
        return entertainerEntityList
                .stream()
                .map(EntertainerConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * konwereter listy obiektów DTO entertainerów do listy obiektów encji entertainerów z hasłami.
     *
     * @param entertainerDTOList - lista obiektów encji DTO entertainerów
     * @param passwords          - hasła entertainerów
     * @return - lista obiektów encji entertainerów
     */
    public static List<EntertainerEntity> convertToListEntity(List<EntertainerDTO> entertainerDTOList,
                                                              List<String> passwords) {
        Map<EntertainerDTO, String> entertainerDTOPasswordMap =
                IntStream.range(0, entertainerDTOList.size())
                        .boxed()
                        .collect(
                                Collectors.toMap(entertainerDTOList::get, passwords::get)
                        );
        return entertainerDTOPasswordMap
                .entrySet()
                .stream()
                .map(x -> EntertainerConverter.convertToEntity(x.getKey(), x.getValue()))
                .collect(Collectors.toList());
    }
}
