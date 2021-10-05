package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers;


import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EjbLoggerInterceptor;

import javax.annotation.Resource;
import javax.annotation.security.RunAs;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.Interceptors;


/**
 * Klasa kontrolująca niezweryfikowane konta w systemie
 * jako singleton wystepuje tylko jedna instancja tej klasy
 */
@Startup
@Singleton
@RunAs("Management")
@Interceptors(EjbLoggerInterceptor.class)
public class SystemScheduler {

    @Inject
    UserManagerLocal userManager;

    @Resource(name = "systemSchedulerHour")
    private Integer systemSchedulerHour;


    /**
     * Metoda dzięki adnotacji schedule co 12 godzin sprawdza czy
     * niezweryfikowane konta zostały utworzone więcej niż dobę temu a następnie je usuwa
     */
    @Schedule(
            minute = "*/1",
            hour = "*",
            info = "deleteUserScheduler")
    public void deleteUnverifiedUsers() throws AbstractAppException {
        userManager.deleteUnverifiedUsers(systemSchedulerHour);
    }
}
