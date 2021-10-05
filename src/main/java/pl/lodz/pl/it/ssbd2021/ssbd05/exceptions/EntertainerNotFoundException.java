package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

public class EntertainerNotFoundException extends AbstractAppException {

    public static final String ENTERTAINER_NOT_FOUND = "Entertainer with id: %s does not exist";
    public static final String ENTERTAINER_NOT_FOUND_NO_ID = "Entertainer does not exist";

    private EntertainerNotFoundException(String message) {
        super(message);
    }

    public static EntertainerNotFoundException entertainerWithProvidedIdNotFoundException(long id) {
        return new EntertainerNotFoundException(String.format(ENTERTAINER_NOT_FOUND, id));
    }

    public static EntertainerNotFoundException entertainerNotFoundException() {
        return new EntertainerNotFoundException(String.format(ENTERTAINER_NOT_FOUND_NO_ID));
    }
}
