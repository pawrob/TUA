package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.AccessLevel;
import lombok.Setter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;

@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "access_level", discriminatorType = DiscriminatorType.STRING, length = 16)
@Table(name = "access_level", schema = "public")
@EntityListeners(EntitiesLogger.class)
public abstract class AccessLevelEntity extends AbstractEntity {
    @Setter(AccessLevel.PROTECTED)
    private String accessLevel;
    private boolean isActive = true;
    private UserEntity user;

    @Basic
    @Column(name = "access_level", nullable = false, length = 16)
    public String getAccessLevel() {
        return accessLevel;
    }

    @Basic
    @Column(name = "is_active", nullable = false)
    public boolean isActive() {
        return isActive;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    public UserEntity getUser() {
        return user;
    }

    public AccessLevelEntity() {
    }

    public AccessLevelEntity(boolean isActive, UserEntity userEntity) {
        this.setActive(isActive);
        this.setUser(userEntity);
    }
}
