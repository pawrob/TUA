package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

/**
 * Klasa obsługująca wyjątki aplikacyjne rzucane w przypadku błędnego tokenu uwierzytelnienia.
 */
public class InvalidTokenException extends AbstractAppException {

    public static final String INVALID_TOKEN = "User with identifier: %s have different token";
    public static final String TOKEN_EXPIRED = "Token is not valid any more";

    private InvalidTokenException(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku błędnego tokenu
     *
     * @param id - identyfikator użytkownika
     * @return klucz błędu
     */
    public static InvalidTokenException createInvalidTokenException(long id) {
        return new InvalidTokenException(String.format(INVALID_TOKEN, id));
    }

    /**
     * Wyjątek wyrzucany w przypadku błędnego tokenu
     *
     * @return klucz błędu
     */
    public static InvalidTokenException createTokenExpiredException() {
        return new InvalidTokenException(TOKEN_EXPIRED);
    }
}
