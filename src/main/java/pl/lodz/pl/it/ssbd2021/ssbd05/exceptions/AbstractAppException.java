package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

import lombok.Getter;

import javax.ejb.ApplicationException;
import java.util.Locale;

/**
 * Klasa abstrakcyjna, rozszerzana przez wszystkie pozostałe klasy wyjątków.
 */
@ApplicationException(rollback = true)
public abstract class AbstractAppException extends Exception {
    @Getter
    private final String key = getClass().getSimpleName()
            .replaceAll("AppException$", "")
            .replaceAll("([a-z])([A-Z])", "$1_$2")
            .toUpperCase(Locale.ROOT);

    @Override
    public String getMessage() {
        return getKey() +
                (super.getMessage() != null ? ": " + super.getMessage() : "");
    }

    protected AbstractAppException() {
    }

    protected AbstractAppException(String message) {
        super(message);
    }

    protected AbstractAppException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return "AbstractAppException{" +
                "key='" + key + '\'' +
                '}';
    }
}
