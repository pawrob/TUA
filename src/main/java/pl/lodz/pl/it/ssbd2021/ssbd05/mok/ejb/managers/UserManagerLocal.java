package pl.lodz.pl.it.ssbd2021.ssbd05.mok.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.AccessLevelDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.AccessLevelEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.PersonalDataEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.SessionLogEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.UserEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import java.util.List;

@Local
public interface UserManagerLocal extends ManagerLocal {
    @RolesAllowed({"Client", "Entertainer", "Management"})
    UserEntity editUserData(PersonalDataEntity newData) throws AbstractAppException;

    @PermitAll
    void logAccessLevelChange(String id, String accessLevel) throws AbstractAppException;

    @PermitAll
    UserEntity verifyUser(long id, String token) throws AbstractAppException;

    @RolesAllowed({"Client", "Entertainer", "Management"})
    UserEntity changePassword(String oldPassword, String newPassword) throws
            AbstractAppException;

    @RolesAllowed({"Client", "Entertainer", "Management"})
    UserEntity changeEmail(long id, String token, String email) throws
            AbstractAppException;

    @RolesAllowed({"Client", "Entertainer", "Management"})
    UserEntity requestChangeEmail(String login, String email) throws
            AbstractAppException;

    @PermitAll
    UserEntity resetPassword(long id, String token, String newPassword) throws
            AbstractAppException;

    @PermitAll
    UserEntity requestResetPassword(String email) throws
            AbstractAppException;

    @RolesAllowed("Management")
    UserEntity activateUserAccount(Long id) throws AbstractAppException;

    @RolesAllowed("Management")
    UserEntity deactivateUserAccount(Long id) throws AbstractAppException;

    //    @RolesAllowed("Management")
    @PermitAll
    List<UserEntity> getAllUsers() throws AbstractAppException;

    @RolesAllowed("Management")
    List<UserEntity> getUserByPieceOfData(String query) throws AbstractAppException;

    @RolesAllowed("Management")
    UserEntity getUser(Long id) throws AbstractAppException;

    @RolesAllowed("Management")
    List<UserEntity> getUsersByPage(Long pageNumber) throws AbstractAppException;

    //todo: Zamienic AccessLevelDTO na AccessLevelEntity!
    @RolesAllowed("Management")
    UserEntity changePrivileges(Long id, List<AccessLevelDTO> accessLevelDTOList) throws AbstractAppException;

    @RolesAllowed("Management")
    void deleteUser(Long id) throws AbstractAppException;

    @PermitAll
    UserEntity getSelfUser() throws AbstractAppException;

    @PermitAll
    void deleteUnverifiedUsers(Integer systemSchedulerHour) throws AbstractAppException;

    @RolesAllowed("Management")
    AccessLevelEntity changeAccessLevelStatus(Long id, boolean status) throws AbstractAppException;

    @PermitAll
    SessionLogEntity getLastFailedLogin(Long id) throws AbstractAppException;

    @PermitAll
    SessionLogEntity getLastSuccessfulLogin(Long id) throws AbstractAppException;

    @PermitAll
    SessionLogEntity getOwnLastFailedLogin() throws AbstractAppException;

    @PermitAll
    SessionLogEntity getOwnLastSuccessfulLogin() throws AbstractAppException;
}
