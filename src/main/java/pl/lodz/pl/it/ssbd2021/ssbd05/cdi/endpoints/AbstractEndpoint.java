package pl.lodz.pl.it.ssbd2021.ssbd05.cdi.endpoints;

import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.UniqueConstraintAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EndpointLoggerInterceptor;

import javax.annotation.Resource;
import javax.ejb.EJBTransactionRolledbackException;
import javax.interceptor.Interceptors;

/**
 * Klasa abstrakcyjna reprezentująca endpoint modułu mok
 */
@Interceptors(EndpointLoggerInterceptor.class)
public abstract class AbstractEndpoint {
    @Resource(name = "maxTransactionRetries")
    private int maxTransactionRetries;

    protected abstract ManagerLocal getManager();

    /**
     * Funkcja ponawiająca wykonanie metody w przypadku odrzucenia transakcji
     * @param function wywoływana metoda
     * @param <R> typ zwracany przez metodę
     * @return obiekt zwracany przez metodę
     * @throws AbstractAppException wyjątek aplikacyjny
     */
    protected <R> R retrying(ThrowingFunction<R> function) throws AbstractAppException {
        int retriesLeft = maxTransactionRetries;
        boolean retryTransaction;
        R result = null;
        Exception lastException;
        do {
            try {
                result = function.run();
                lastException = null;
                retryTransaction = !getManager().isLastCommitted();
            } catch (UniqueConstraintAppException.WrongTermsAppException exception){
                lastException = exception;
                retryTransaction = false;
            }
            catch (AbstractAppException | EJBTransactionRolledbackException exception) {
                lastException = exception;
                retryTransaction = true;
            }
        } while (retryTransaction && --retriesLeft > 0);
        if (lastException != null) {
            if (lastException instanceof AbstractAppException) {
                throw (AbstractAppException) lastException;
            } else {
                throw (EJBTransactionRolledbackException) lastException;
            }
        }
        return result;
    }

    public interface ThrowingFunction<R> {
        R run() throws AbstractAppException;
    }
}

