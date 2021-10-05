package pl.lodz.pl.it.ssbd2021.ssbd05.interceptors;

import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.DatabaseErrorAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.UniqueConstraintAppException;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.PersistenceException;

/**
 * Interceptor mapujący wyjątki PersistenceException
 */
public class PersistenceExceptionInterceptor {

    static final String DB_CONSTRAINT_UNIQUE_LOGIN = "user_login_key";
    static final String DB_CONSTRAINT_UNIQUE_EMAIL = "user_email_key";

    /**
     *
     * Metoda opakowująca wyjątek PersistenceException na wyjątek aplikacyjny
     * @param ictx kontekst wywołania metody
     * @return obiekt zwracany przez metodę
     * @throws Exception ewentualny wyjątek
     */
    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();
        } catch (PersistenceException pe) {
            if (pe.getMessage().contains(DB_CONSTRAINT_UNIQUE_LOGIN))
                throw UniqueConstraintAppException.createLoginTakenException();
            if (pe.getMessage().contains(DB_CONSTRAINT_UNIQUE_EMAIL))
                throw UniqueConstraintAppException.createEmailTakenException();
            else
                throw DatabaseErrorAppException.createDatabaseAppException(pe);
        }
    }
}
