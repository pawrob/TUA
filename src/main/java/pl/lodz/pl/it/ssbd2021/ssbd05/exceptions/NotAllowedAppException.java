package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

/**
 * Klasa obsługująca wyjątki aplikacyjne rzucane w przypadku braku uprawnień.
 */
public class NotAllowedAppException extends AbstractAppException {
    public static final String NOT_ALLOWED = "Operation not allowed";

    public NotAllowedAppException(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku braku uprawnień dla danej operacji
     *
     * @return klucz błędu
     */
    public static NotAllowedAppException createNotAllowedException() {
        return new NotAllowedAppException(NOT_ALLOWED);
    }
}
