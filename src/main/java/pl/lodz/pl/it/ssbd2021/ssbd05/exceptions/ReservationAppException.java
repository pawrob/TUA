package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ReservationAppException extends AbstractAppException {
    private static final String CANNOT_BE_EDITED = "Reservation with id: %d cannot be edited";
    private static final String NOT_ACTUAL = "Reservation with id: %d Ended, Canceled or Already Accepted";
    private static final String NOT_FOUND = "Reservation with id: %d Not Found";
    private static final String NOT_ACCEPTED = "Reservation with id: %d is not Accepted";

    private ReservationAppException(String message) {
        super(message);
    }

    public static ReservationNotActualAppException createReservationNotActualAppException(Long id){
        return new ReservationNotActualAppException(String.format(NOT_ACTUAL, id));
    }
    public static ReservationCannotBeEditedlAppException createReservationCannotBeEditedAppException(Long id) {
        return new ReservationCannotBeEditedlAppException(String.format(CANNOT_BE_EDITED, id));
    }

    public static ReservationNotFoundAppException createReservationNotFoundAppException(Long id){
        return new ReservationNotFoundAppException(String.format(NOT_FOUND, id));
    }

    public static ReservationNotAcceptedAppException createReservationNotAcceptedAppException(Long id) {
        return new ReservationNotAcceptedAppException(String.format(NOT_ACCEPTED, id));
    }

    @ApplicationException(rollback = true)
    public static class ReservationNotActualAppException extends ReservationAppException{

        private ReservationNotActualAppException(String message) {
            super(message);
        }
    }

    @ApplicationException(rollback = true)
    private static class ReservationNotFoundAppException extends ReservationAppException {
        private ReservationNotFoundAppException(String message) {
            super(message);
        }
    }
    @ApplicationException(rollback = true)
    private static class ReservationNotAcceptedAppException extends ReservationAppException{
        private ReservationNotAcceptedAppException(String message) {
            super(message);
        }
    }

    @ApplicationException(rollback = true)
    private static class ReservationCannotBeEditedlAppException extends ReservationAppException {
        private ReservationCannotBeEditedlAppException(String message) {
            super(message);
        }
    }
}
