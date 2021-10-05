package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;
import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ReservationNotFoundAppException extends AbstractAppException {

    public static final String RESERVATION_NOT_EXISTS = "Reservation with identifier: %s does not exist";

    private ReservationNotFoundAppException(String message) {
        super(message);
    }

    public static ReservationNotFoundAppException updateRatingOfReservationWithProvidedIdNotFoundException(long id) {
        return new ReservationNotFoundAppException(String.format(RESERVATION_NOT_EXISTS, id));
    }

//    public static ReservationNotFoundAppException createUserWithProvidedEmailNotFoundException(String email) {
//        return new ReservationNotFoundAppException(String.format(USER_WITH_EMAIL_NOT_EXISTS, email));
//    }
//
//    public static ReservationNotFoundAppException createUserWithProvidedLoginNotFoundException(String login) {
//        return new ReservationNotFoundAppException(String.format(USER_WITH_LOGIN_NOT_EXISTS, login));
//    }


    public ReservationNotFoundAppException() {
    }
}
