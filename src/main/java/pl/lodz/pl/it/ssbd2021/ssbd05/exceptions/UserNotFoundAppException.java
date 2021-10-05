package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class UserNotFoundAppException extends AbstractAppException {

    public static final String USER_NOT_EXISTS = "User with identifier: %s does not exist";
    public static final String USER_WITH_EMAIL_NOT_EXISTS = "User with email: %s does not exist";
    public static final String USER_WITH_LOGIN_NOT_EXISTS = "User with email: %s does not exist";

    private UserNotFoundAppException(String message) {
        super(message);
    }

    public static UserNotFoundAppException createUserWithProvidedIdNotFoundException(long id) {
        return new UserNotFoundAppException(String.format(USER_NOT_EXISTS, id));
    }

    public static UserNotFoundAppException createUserWithProvidedEmailNotFoundException(String email) {
        return new UserNotFoundAppException(String.format(USER_WITH_EMAIL_NOT_EXISTS, email));
    }

    public static UserNotFoundAppException createUserWithProvidedLoginNotFoundException(String login) {
        return new UserNotFoundAppException(String.format(USER_WITH_LOGIN_NOT_EXISTS, login));
    }

    public static final String USER_NOT_FOUND = "User not found";

    public UserNotFoundAppException() {
    }

    public static UserNotFoundAppException activateUserNotFoundException() {
        return new UserNotFoundAppException(USER_NOT_FOUND);
    }

    public static UserNotFoundAppException userWithAccessLevelNotFoundException() {
        return new UserNotFoundAppException(USER_NOT_FOUND);
    }
}
