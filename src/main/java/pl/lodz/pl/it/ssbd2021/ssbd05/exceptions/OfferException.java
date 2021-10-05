package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class OfferException extends AbstractAppException {
    private static final String NOT_AVAILABLE = "Offer with id: %b is not available";
    private static final String NOT_ACCEPTED = "Offer with id: %d is not Accepted";

    private OfferException(String message) {
        super(message);
    }

    public static class OfferNotAvailableException extends OfferException {

        private OfferNotAvailableException(String message) {
            super(message);
        }
    }

    public static class OfferNotAcceptedAppException extends OfferException {

        private OfferNotAcceptedAppException(String message) {
            super(message);
        }
    }

    public static OfferNotAvailableException createOfferNotAvailableException(Long id) {
        return new OfferNotAvailableException(String.format(NOT_AVAILABLE, id));
    }

    public static OfferNotAcceptedAppException createOfferNotAcceptedAppException(Long id) {
        return new OfferNotAcceptedAppException(String.format(NOT_ACCEPTED, id));
    }
}
