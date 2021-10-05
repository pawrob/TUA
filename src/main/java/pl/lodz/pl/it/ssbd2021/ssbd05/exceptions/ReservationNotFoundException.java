package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

public class ReservationNotFoundException extends AbstractAppException {

    public static final String RESERVATION_NOT_FOUND = " Offer not found";

    public ReservationNotFoundException() {
    }

    private ReservationNotFoundException(String message) {
        super(message);
    }

    public static ReservationNotFoundException createReservationWithProvidedIdNotFoundException(long id) {
        return new ReservationNotFoundException(String.format(RESERVATION_NOT_FOUND, id));
    }
}