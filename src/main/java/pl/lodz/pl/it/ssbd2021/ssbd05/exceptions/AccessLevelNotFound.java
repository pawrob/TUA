package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

/**
 * Klasa obsługująca wyjątki aplikacyjne rzucane w przypadku braku danego dostępu.
 */
public class AccessLevelNotFound extends AbstractAppException {
    public static final String ACCESS_LEVEL_NOT_FOUND = "User with identifier: %s does not have %s access level";

    private AccessLevelNotFound(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku nie znalezienia poziomu dostępu
     * @param id - identyfikator
     * @return  klucz błędu
     */
    public static AccessLevelNotFound createAccessLevelForUserNotFound(long id, String accessLevel) {
        return new AccessLevelNotFound(String.format(ACCESS_LEVEL_NOT_FOUND, id, accessLevel));
    }
}
