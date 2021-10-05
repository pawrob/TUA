package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ClientDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ClientEntity;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Klasa udostępniająca metody konwersji miedzy obiektami typu ClientDTo oraz ClientEntity
 */
public class ClientConverter {

    /**
     * Metoda pozwalająca na konwersję obiektu typu ClientEntity na obiekt typu ClientDTO
     * @param e
     * @return obietk DTO zawierający informacje o użytkowniku
     */
    public static ClientDTO convertToDTO(ClientEntity e) {
        return new ClientDTO(
                e.getId(),
                e.getVersion(),
                e.getUser().getLogin(),
                e.getUser().getEmail(),
                e.getUser().isActive(),
                e.getUser().isVerified()
        );
    }

    /**
     * Metoda pozwalająca na konwersję kolekcji obiektów typu ClientEntity na kolekcję obiektów typu Client DTO
     * @param e
     * @return lista obietków DTO zawierających informacje o użytkownikach
     */
    public static Collection<ClientDTO> convertToListDTO(Collection<ClientEntity> e) {
        return e.stream()
                .map(ClientConverter::convertToDTO)
                .collect(Collectors.toList());
    }
}
