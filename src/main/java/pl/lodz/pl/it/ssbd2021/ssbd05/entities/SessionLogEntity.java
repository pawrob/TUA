package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Setter
@Entity
@Table(name = "session_log", schema = "public")
@NamedQueries({
        @NamedQuery(name = "UserEntity.lastFailedLogin", query = "SELECT  k FROM SessionLogEntity k WHERE k.user.id = :id " +
                "AND k.successful = false ORDER BY k.actionTimestamp desc"),
        @NamedQuery(name = "UserEntity.lastSuccessfulLogin", query = "SELECT  k FROM SessionLogEntity k WHERE k.user.id = :id " +
                "AND k.successful = true ORDER BY k.actionTimestamp desc")
})
@AllArgsConstructor
@NoArgsConstructor
public class SessionLogEntity extends AbstractEntity {
    private OffsetDateTime actionTimestamp;
    private String ipAddress;
    private boolean isSuccessful;
    private UserEntity user;


    @Basic
    @Column(name = "action_timestamp", nullable = false)
    public OffsetDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    @Basic
    @Column(name = "ip_address", nullable = false)
    public String getIpAddress() {
        return ipAddress;
    }

    @Basic
    @Column(name = "is_successful", nullable = false)
    public boolean isSuccessful() {
        return isSuccessful;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public UserEntity getUser() {
        return user;
    }
}
