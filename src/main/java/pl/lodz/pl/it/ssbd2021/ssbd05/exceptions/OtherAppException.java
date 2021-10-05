package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

import javax.ejb.ApplicationException;

/**
 * Klasa obsługująca inne wyjątki aplikacyjne.
 */
@ApplicationException(rollback = true)
public class OtherAppException extends AbstractAppException {

    public static final String WRONG_DATAS = "Pair of date is not a correct period time";

    public OtherAppException(String message) {
        super(message);
    }

    public OtherAppException(Throwable cause) {
        super("Other problem", cause);
    }

    public static OtherAppException createInvalidDatasExeception() {
        return new OtherAppException(WRONG_DATAS);
    }
}
