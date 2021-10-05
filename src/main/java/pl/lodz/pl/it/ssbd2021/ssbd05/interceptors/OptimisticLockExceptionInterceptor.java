package pl.lodz.pl.it.ssbd2021.ssbd05.interceptors;

import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.OptimisticLockAppException;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.OptimisticLockException;

/**
 * Interceptor mapujący wyjątki OptimisticLock
 */
public class OptimisticLockExceptionInterceptor {

    /**
     * Metoda opakowująca wyjątek OptimisticLock na wyjątek aplikacyjny
     * @param ictx kontekst wywołania metody
     * @return obiekt zwracany przez metodę
     * @throws Exception ewentualny wyjątek
     */
    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();
        } catch (
                OptimisticLockException ole) {
            throw OptimisticLockAppException.createOptimisticLockAppException();
        }
    }
}
