package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.Setter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;


@Setter
@Entity
@Table(name = "user", schema = "ssbd05")
@NamedQueries({
        @NamedQuery(name = "UserEntity.findByEmail", query = "SELECT k FROM UserEntity k WHERE k.email = :email"),
        @NamedQuery(name = "UserEntity.findByLogin", query = "SELECT k FROM UserEntity k WHERE k.login = :login")
})
@EntityListeners(EntitiesLogger.class)
public class UserEntity extends AbstractEntity {
    private String login;
    private String password;
    private String email;
    private boolean isActive = true;
    private boolean isVerified = false;
    private String passwordResetToken;
    private Timestamp tokenTimestamp;
    private int failedLogin = 0;
    private Collection<AccessLevelEntity> accessLevels = new ArrayList<>();
    private PersonalDataEntity personalData;


    @Basic
    @Column(name = "login", nullable = false, length = 16)
    public String getLogin() {
        return login;
    }

    @Basic
    @Column(name = "password", nullable = false, length = 60)
    public String getPassword() {
        return password;
    }

    @Basic
    @Column(name = "email", nullable = false, length = 64)
    public String getEmail() {
        return email;
    }

    @Basic
    @Column(name = "is_active", nullable = false)//, columnDefinition = "boolean default true")
    public boolean isActive() {
        return isActive;
    }

    @Basic
    @Column(name = "is_verified", nullable = false)//, columnDefinition = "boolean default false")
    public boolean isVerified() {
        return isVerified;
    }

    @Basic
    @Column(name = "password_reset_token", length = 64)
    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    @Basic
    @Column(name = "token_timestamp", nullable = false)
    public Timestamp getTokenTimestamp() {
        return tokenTimestamp;
    }

    @Basic
    @Column(name = "failed_login", nullable = false)
    public int getFailedLogin() {
        return failedLogin;
    }

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    public Collection<AccessLevelEntity> getAccessLevels() {
        return accessLevels;
    }

    @OneToOne(mappedBy = "user", cascade = {CascadeType.ALL})
    public PersonalDataEntity getPersonalData() {
        return personalData;
    }

    public UserEntity() {
    }

    public UserEntity(String login, String email, String password) {
        this.login = login;
        this.password = password;
        this.email = email;
    }
}
