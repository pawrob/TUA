package pl.lodz.pl.it.ssbd2021.ssbd05.util.logger;

import pl.lodz.pl.it.ssbd2021.ssbd05.entities.SessionLogEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.SessionLogEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.facades.UserEntityMokFacade;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.MailManager;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;

@PermitAll
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class SessionLogger {

    @Inject
    SessionLogEntityMokFacade sessionLogEntityAuthFacade;

    @Inject
    UserEntityMokFacade userEntityFacade;

    @Inject
    private MailManager mailManager;

    @Resource(name = "maxFaliluresLogin")
    private Integer maxFailuresLogin;

    @Inject
    private HttpServletRequest request;

    public Object saveAuthenticationInfo(CredentialValidationResult result, String username) throws AbstractAppException {

        var userEntity = userEntityFacade.findByLogin(username);

        if (result.getStatus().equals(CredentialValidationResult.Status.VALID)) {

            userEntity.setFailedLogin(0);
            userEntityFacade.edit(userEntity);
            userEntityFacade.flush();
            for (String group : result.getCallerGroups()) {
                if (group.equals("MANAGEMENT")) {
                    String ip = "IP: " + request.getRemoteAddr();
                    mailManager.createAndSendEmailFromTemplate(userEntity, "titleAdminLogin", "titleAdminLogin", "content", ip);

                }
            }
        } else if (result.getStatus().equals(CredentialValidationResult.Status.INVALID)
                && null != userEntity) {
            boolean isActive = userEntity.isActive();
            int failedLoginCount = userEntity.getFailedLogin() + 1;
            if (failedLoginCount > maxFailuresLogin && isActive) {
                var buttonText = "noButton";
                userEntity.setActive(false);
                mailManager.createAndSendEmailFromTemplate(userEntity, "headerBlockAccount", "headerBlockAccount", buttonText, "footerBlockAccount");
            } else {
                userEntity.setFailedLogin(failedLoginCount);
            }
            userEntityFacade.edit(userEntity);
            userEntityFacade.flush();
        }

        var log = new SessionLogEntity();
        log.setUser(userEntity);
        log.setActionTimestamp(OffsetDateTime.now());
        log.setIpAddress(request.getRemoteAddr());
        sessionLogEntityAuthFacade.create(log);
        log.setSuccessful(result.getStatus().equals(CredentialValidationResult.Status.VALID));
        return result;
    }
}
