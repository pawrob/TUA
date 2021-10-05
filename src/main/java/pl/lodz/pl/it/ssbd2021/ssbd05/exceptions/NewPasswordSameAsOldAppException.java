package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

/**
 * Klasa obsługująca wyjątki aplikacyjne rzucane w przypadku identyczności danych haseł.
 */
public class NewPasswordSameAsOldAppException extends PasswordAppException {
    public static final String NEW_PASSWORD_SAME_AS_OLD = "New password can't be the same as old one";

    private NewPasswordSameAsOldAppException(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku identyczności podanego hasła z aktualnym hasłem
     *
     * @return klucz błędu
     */
    public static NewPasswordSameAsOldAppException createNewPasswordSameAsOldAppException() {
        return new NewPasswordSameAsOldAppException(NEW_PASSWORD_SAME_AS_OLD);
    }

}
