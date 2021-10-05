package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

/**
 * Klasa obsługująca wyjątki aplikacyjne dotyczące blokad optymistycznych.
 */
public class OptimisticLockAppException extends AbstractAppException {

    static final String OPTIMISTIC_LOCK_MESSAGE = "Entity has beed changed or deleted";

    private OptimisticLockAppException(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku naruszenia spójności danych
     *
     * @return klucz błędu
     */
    public static OptimisticLockAppException createOptimisticLockAppException() {
        return new OptimisticLockAppException(OPTIMISTIC_LOCK_MESSAGE);
    }
}
