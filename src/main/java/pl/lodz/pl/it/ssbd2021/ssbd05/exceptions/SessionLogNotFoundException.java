package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

public class SessionLogNotFoundException extends AbstractAppException {

    public static final String LOG_NOT_FOUND = "Log for this operation not found";
    public static final String SUCCESSFUL_LOG_NOT_FOUND = "Log for this operation not found";

    private SessionLogNotFoundException(String message) {
        super(message);
    }

    public static SessionLogNotFoundException createSessionLogNotFoundException() {
        return new SessionLogNotFoundException(String.format(LOG_NOT_FOUND));
    }

    public static SessionLogNotFoundException createSessionSuccessfulLogNotFoundException() {
        return new SessionLogNotFoundException(String.format(SUCCESSFUL_LOG_NOT_FOUND));
    }

}
