package pl.lodz.pl.it.ssbd2021.ssbd05.util.logger;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class TransactionLogger {
    @Getter
    private final Logger logger;
    private String prefix = "";

    private String prepareLog(String log) {
        return "[" + prefix + "] " + log;
    }

    public TransactionLogger() {
        prefix = UUID.randomUUID().toString();
        logger = LogManager.getLogger(this.getClass());
    }

    public void logTransactionBegin() {
        logger.trace(prepareLog("transaction begin"));
    }

    /**
     * Metoda dodająca do wpisu informacje o zakonczonej transakcji.
     *
     * @param committed informacja o tym czy transakcja została zatwierdzona
     */
    public void logTransactionEnd(boolean committed) {
        String log = "transaction end with " + (committed ? "commit" : "rollback");
        logger.trace(prepareLog(log));
    }
}

