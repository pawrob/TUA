package pl.lodz.pl.it.ssbd2021.ssbd05.util.logger;

import lombok.SneakyThrows;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AbstractEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.QueryLogEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.QueryLogEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.UserEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.QueryLogEntityMooFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.facades.UserEntityMooFacade;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionSynchronizationRegistry;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
@PermitAll
public class EntitiesLogger {

    @Inject
    UserEntityMokFacade userMokFacade;

    @Inject
    UserEntityMooFacade userMooFacade;
    @Inject
    QueryLogEntityMokFacade queryMokLogFacade;
    @Inject
    QueryLogEntityMooFacade queryMooLogFacade;
    @Resource
    TransactionSynchronizationRegistry tsr;
    @Inject
    private HttpServletRequest request;

    private String getFunctionalModule() {
        return (String) tsr.getResource("FUNC_MODULE");
    }

    private UserEntity getUser() throws AbstractAppException {
        if ("moo".equals(getFunctionalModule())) {
            return userMooFacade.findByLogin(request.getRemoteUser());
        } else {
            return userMokFacade.findByLogin(request.getRemoteUser());
        }
    }

    private void createQueryLogInDatabase(QueryLogEntity logEntity) {
//        var runnables = (List<Runnable>) tsr.getResource("RUNNABLES");
//        if( runnables != null){
//            if ("moo".equals(getFunctionalModule())) {
//                runnables.add(new Runnable() {
//                    @SneakyThrows
//                    @Override
//                    public void run() {
//                        queryMooLogFacade.create(logEntity);
//                        queryMooLogFacade.flush();
//                    }
//                });
//            }
//            else {
//                runnables.add(new Runnable() {
//                    @SneakyThrows
//                    @Override
//                    public void run() {
//                        queryMokLogFacade.create(logEntity);
//                        queryMokLogFacade.flush();
//                    }
//                });
//            }
//        }

    }

    @PrePersist
    void onPrePersist(AbstractEntity entity) throws AbstractAppException {
        String query = "CREATED [" + entity.getClass().getSimpleName() + "] WITH ID [" + entity.getId() + "]";
        QueryLogEntity queryLog = new QueryLogEntity();

        queryLog.setActionTimestamp(Timestamp.from(Instant.now()));
        queryLog.setQuery(query);
        queryLog.setAffectedTable(entity.getClass().getSimpleName().replace("Entity", ""));
        queryLog.setUser(getUser());
        queryLog.setModule(getFunctionalModule());

        createQueryLogInDatabase(queryLog);
    }

    @PreUpdate
    void onPreUpdate(AbstractEntity entity) throws AbstractAppException {
        String query = "UPDATED [" + entity.getClass().getSimpleName() + "] WITH ID [" + entity.getId() + "]";
        QueryLogEntity queryLog = new QueryLogEntity();

        queryLog.setActionTimestamp(Timestamp.from(Instant.now()));
        queryLog.setQuery(query);
        queryLog.setAffectedTable(entity.getClass().getSimpleName().replace("Entity", ""));
        queryLog.setUser(getUser());
        queryLog.setModule(getFunctionalModule());

        createQueryLogInDatabase(queryLog);
    }

    @PreRemove
    void onPreRemove(AbstractEntity entity) throws AbstractAppException {
        String query = "REMOVED [" + entity.getClass().getSimpleName() + "] WITH ID [" + entity.getId() + "]";
        QueryLogEntity queryLog = new QueryLogEntity();

        queryLog.setActionTimestamp(Timestamp.from(Instant.now()));
        queryLog.setQuery(query);
        queryLog.setAffectedTable(entity.getClass().getSimpleName().replace("Entity", ""));
        queryLog.setModule(getFunctionalModule());

        createQueryLogInDatabase(queryLog);
    }
}
