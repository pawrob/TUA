package pl.lodz.pl.it.ssbd2021.ssbd05.exceptions;

/**
 * Klasa obsługująca wyjątki aplikacyjne dotyczące bazy danych.
 */
public class DatabaseErrorAppException extends AbstractAppException {
    static final String DB_CONNECTION_ERROR = "Unable to connect with Database";

    private DatabaseErrorAppException(String message, Throwable cause) {
        super(message, cause);
    }

    private DatabaseErrorAppException(String message) {
        super(message);
    }

    /**
     * Wyjątek wyrzucany w przypadku błędu przekazanego przez bazę danych
     * @param ex - wyjatek
     * @return  klucz błędu
     */
    public static DatabaseErrorAppException createDatabaseAppException(Throwable ex) {
        return new DatabaseErrorAppException(DB_CONNECTION_ERROR, ex);
    }
    /**
     * Wyjątek wyrzucany w przypadku błędu od bazy danych
     * @return  klucz błędu
     */
    public static DatabaseErrorAppException createDatabaseAppException(){
        return new DatabaseErrorAppException(DB_CONNECTION_ERROR);
    }
}
