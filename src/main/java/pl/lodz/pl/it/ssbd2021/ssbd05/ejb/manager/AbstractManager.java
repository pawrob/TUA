package pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager;

import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.TransactionLogger;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.TransactionSynchronizationRegistry;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa abstrakcyjna zawierająca implementację rejestrowania wpisów do dziennika zdarzeń obejmująctch transakcje.
 */
public abstract class AbstractManager implements SessionSynchronization, ManagerLocal {
    private final TransactionLogger transactionLogger = new TransactionLogger();
    private boolean lastCommitted;

    @Resource
    TransactionSynchronizationRegistry tsr;

    /**
     * Metoda dodająca wpis o rozpoczeniu transakcji.
     *
     * @throws EJBException
     * @throws RemoteException
     */
    @Override
    public void afterBegin() throws EJBException, RemoteException {
        transactionLogger.logTransactionBegin();
        tsr.putResource("FUNC_MODULE", getFunctionalModuleName());
        tsr.putResource("RUNNABLES", new ArrayList<>());
    }

    protected abstract String getFunctionalModuleName();

    @Override
    public void beforeCompletion() throws EJBException {
        var runnables = (List<Runnable>) tsr.getResource("RUNNABLES");
        for (int i = 0, len = runnables.size(); i < len; ++i) {
            runnables.get(i).run();
        }
    }


    /**
     * Metoda dodająca wpis po zakończeniu transakcji.
     *
     * @param committed - wartość logiczna opisująca zakończenie transkacji
     * @throws EJBException
     */
    @Override
    public void afterCompletion(boolean committed) throws EJBException {
        lastCommitted = committed;
        transactionLogger.logTransactionEnd(committed);
    }

    /**
     * Metoda określająca czy transakcja zakonczyla sie poprawnie czy nie.
     *
     * @return wartość logiczna definiująca status zakończenia transakcji
     */
    @Override
    @PermitAll
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean isLastCommitted() {
        return lastCommitted;
    }
}
