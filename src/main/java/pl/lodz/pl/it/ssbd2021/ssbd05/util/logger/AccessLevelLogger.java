package pl.lodz.pl.it.ssbd2021.ssbd05.util.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;

@PermitAll
@ApplicationScoped
public class AccessLevelLogger {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public void logAccessLevelChange(String login, String accessLevel) {

        String log = "User with login " + login + "changed his accessLevel view to " + accessLevel;
        logger.trace(log);
    }

    public AccessLevelLogger() {
    }
}
