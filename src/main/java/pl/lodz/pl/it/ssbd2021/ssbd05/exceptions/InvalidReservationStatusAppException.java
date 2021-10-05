package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;
import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class InvalidReservationStatusAppException extends AbstractAppException {

    public static final String INVALID_RESERVATION_STATUS = "Reservation with identifier: %s can not be rated";

    private InvalidReservationStatusAppException(String message) {
        super(message);
    }

    public static InvalidReservationStatusAppException updateRatingOfReservationWithProvidedIdNotFoundException(long id) {
        return new InvalidReservationStatusAppException(String.format(INVALID_RESERVATION_STATUS, id));
    }

//    public static ReservationNotFoundAppException createUserWithProvidedEmailNotFoundException(String email) {
//        return new ReservationNotFoundAppException(String.format(USER_WITH_EMAIL_NOT_EXISTS, email));
//    }
//
//    public static ReservationNotFoundAppException createUserWithProvidedLoginNotFoundException(String login) {
//        return new ReservationNotFoundAppException(String.format(USER_WITH_LOGIN_NOT_EXISTS, login));
//    }


    public InvalidReservationStatusAppException() {
    }
}
