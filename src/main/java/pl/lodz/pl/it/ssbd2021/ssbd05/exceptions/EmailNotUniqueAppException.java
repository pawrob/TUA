package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

/**
 * Klasa obsługująca wyjątki aplikacyjne rzucane w przypadku braku unikalności adresu e-mail.
 */
public class EmailNotUniqueAppException extends AbstractAppException {

    public static final String NOT_UNIQUE_EMAIL = "Email can not be same as before";

    private EmailNotUniqueAppException(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku gdy adres email jest juz wykorzystywany
     *
     * @return klucz błędu
     */
    public static EmailNotUniqueAppException createEmailNotUniqueException() {
        return new EmailNotUniqueAppException(String.format(NOT_UNIQUE_EMAIL));
    }


}