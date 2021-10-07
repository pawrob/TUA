package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.Setter;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Setter
@Entity
@Table(name = "access_level_change_log", schema = "ssbd05")
public class AccessLevelChangeLogEntity extends AbstractEntity {
    private OffsetDateTime actionTimestamp;
    private String accessLevel;
    private UserEntity user;

    @Basic
    @Column(name = "action_timestamp", nullable = false)
    public OffsetDateTime getActionTimestamp() {
        return actionTimestamp;
    }

    @Basic
    @Column(name = "access_level", nullable = false, length = 15)
    public String getAccessLevel() {
        return accessLevel;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public UserEntity getUser() {
        return user;
    }
}
