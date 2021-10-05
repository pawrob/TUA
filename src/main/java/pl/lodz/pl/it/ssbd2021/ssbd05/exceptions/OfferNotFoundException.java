package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

public class OfferNotFoundException extends AbstractAppException {

    public static final String OFFER_NOT_FOUND = " Offer not found";

    public OfferNotFoundException() {
    }

    private OfferNotFoundException(String message) {
        super(message);
    }

    public static OfferNotFoundException createOfferWithProvidedIdNotFoundException(long id) {
        return new OfferNotFoundException(String.format(OFFER_NOT_FOUND, id));
    }
}