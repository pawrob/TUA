package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

import javax.ejb.ApplicationException;

/**
 * Klasa obsługująca wyjątki aplikacyjne rzucane w przypadku niespełnionych ograniczeń.
 */
@ApplicationException(rollback = true)
public class UniqueConstraintAppException extends AbstractAppException {

    public static final String LOGIN_TAKEN = "Login is already taken";
    public static final String EMAIL_TAKEN = "Email is already taken";
    public static final String FAVOURITE_ALREADY = "The offer is already added to favourites";
    public static final String NOT_FAVOURITE_ALREADY = "The offer is already removed from favourites";
    public static final String WRONG_TERMS = "Times periods are invalid";


    /**
     * Wyjątek wyrzucany w przypadku naruszeń ograniczeń składniowych
     *
     * @param message
     */
    private UniqueConstraintAppException(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku gdy podany email jest juz zajęty
     *
     * @return klucz błędu
     */
    public static LoginTakenAppException createLoginTakenException() {
        return new LoginTakenAppException(LOGIN_TAKEN);
    }

    /**
     * Wyjątek wyrzucany w przypadku gdy podany email jest juz zajęty
     *
     * @return klucz błędu
     */
    public static EmailTakenAppException createEmailTakenException() {
        return new EmailTakenAppException(EMAIL_TAKEN);
    }

    public static FavouriteAlreadyAppException createFavouriteAlreadyException() {
        return new FavouriteAlreadyAppException(FAVOURITE_ALREADY);
    }

    public static NotFavouriteAlreadyAppException createNotFavouriteAlreadyException() {
        return new NotFavouriteAlreadyAppException(NOT_FAVOURITE_ALREADY);
    }

    /**
     * Wyjątek wyrzucany w przypadku gdy podany email jest juz zajęty
     */
    public static WrongTermsAppException createWrongTermsException() {
        return new WrongTermsAppException(WRONG_TERMS);
    }

    @ApplicationException(rollback = true)
    public static class LoginTakenAppException extends UniqueConstraintAppException{

        private LoginTakenAppException(String message) {
            super(message);
        }
    }

    /**
     * Wyjątek wyrzucany w przypadku gdy podany email jest juz zajęty
     */
    @ApplicationException(rollback = true)
    public static class EmailTakenAppException extends UniqueConstraintAppException{

        private EmailTakenAppException(String message) {
            super(message);
        }
    }

    @ApplicationException(rollback = true)
    public static class WrongTermsAppException extends UniqueConstraintAppException {
        public WrongTermsAppException(String message) {
            super(message);
        }
    }

    @ApplicationException(rollback = true)
    public static class FavouriteAlreadyAppException extends UniqueConstraintAppException{

        private FavouriteAlreadyAppException(String message) {
            super(message);
        }
    }

    @ApplicationException(rollback = true)
    public static class NotFavouriteAlreadyAppException extends UniqueConstraintAppException{

        private NotFavouriteAlreadyAppException(String message) {
            super(message);
        }
    }
}
