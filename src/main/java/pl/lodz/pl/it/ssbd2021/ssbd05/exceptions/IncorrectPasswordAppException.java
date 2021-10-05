package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

/**
 * Klasa obsługująca wyjątki aplikacyjne rzucane w przypadku nieprawidłowego hasła.
 */
public class IncorrectPasswordAppException extends PasswordAppException {
    public static final String NEW_PASSWORD_SAME_AS_OLD = "Password is incorrect";

    private IncorrectPasswordAppException(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku gdy podane zostanie błędne hasło
     *
     * @return klucz błędu
     */
    public static IncorrectPasswordAppException createIncorrectPasswordAppException() {
        return new IncorrectPasswordAppException(NEW_PASSWORD_SAME_AS_OLD);
    }
}
