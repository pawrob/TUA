package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerUnavailabilityDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.EntertainerUnavailabilityEntireDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.EntertainerUnavailabilityEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import java.util.List;

/**
 * Komponent EJB z logiką biznesową dotyczącą Pracownika Działu Rozrywki w Module Obsługi Ofert (MOO).
 */
@Local
public interface EntertainerMooManagerLocal extends ManagerLocal {

    @RolesAllowed("ENTERTAINER")
    EntertainerUnavailabilityEntity updateUnavailability(EntertainerUnavailabilityEntireDTO unavailabilityDTO) throws AbstractAppException;

    @PermitAll
    List<EntertainerUnavailabilityEntity> getUnavailabilities() throws AbstractAppException;

    @RolesAllowed("ENTERTAINER")
    List<EntertainerUnavailabilityEntity> getMyUnavailabilities() throws AbstractAppException;

    EntertainerEntity getEntertainerByUsername(String username) throws AbstractAppException;

    @RolesAllowed("ENTERTAINER")
    EntertainerUnavailabilityEntity createUnavailability(EntertainerUnavailabilityDTO unavailability) throws AbstractAppException;

    /**
     * Metoda pozwalająca wczytać opis w profilu ustawiony przez PDR.
     * <p>
     * Dostępna dla użytkowników z jedną z ról: "Client", "Entertainer", "Management"
     *
     * @param id identyfikator PDR
     * @return ciąg znaków reprezentujący opis w profilu PDR
     * @throws AbstractAppException
     */
    String getDescription(Long id) throws AbstractAppException;

    /**
     * Metoda pozwalająca wczytać opis w profilu obecnie uwierzytelnionego PDR.
     * <p>
     * Dostępna dla użytkownika z rolą "Entertainer".
     *
     * @return ciąg znaków reprezentujący opis w profilu PDR
     * @throws AbstractAppException
     */
    String getSelfDescription() throws AbstractAppException;

    /**
     * Metoda pozwalająca zaktualizować opis w profilu obecnie uwierzytelnionego PDR.
     * <p>
     * Dostępna dla użytkownika z rolą "Entertainer".
     *
     * @return ciąg znaków reprezentujący opis w profilu PDR
     * @throws AbstractAppException
     */
    String updateDescription(String description) throws AbstractAppException;

    /**
     * Metoda pozwalająca pobrać podstawowe informacje o PDR.
     * <p>
     * Dostępna dla użytkowników z jedną z ról: "Client", "Entertainer", "Management"
     *
     * @param id identyfikator PDR
     * @return informacje o PDR
     * @throws AbstractAppException
     */
    EntertainerEntity getBasicEntertainerInfo(Long id) throws AbstractAppException;

    /**
     * Metoda pozwalająca pobrać okresy niedostępności PDR.
     * <p>
     * Dostępna dla użytkownika z rolą "Client".
     *
     * @param id identyfikator PDR
     * @return lista okresów niedostępności PDR
     * @throws AbstractAppException
     */
    List<EntertainerUnavailabilityEntity> getUnavailability(Long id) throws AbstractAppException;

    EntertainerUnavailabilityEntity updateUnavailability(Long id, EntertainerUnavailabilityEntity unavailabilityEntity);

    EntertainerEntity updateDescription(Long id, String description);

    EntertainerUnavailabilityEntity updateUnavailabilityStatus(long id, boolean status) throws AbstractAppException;
}
