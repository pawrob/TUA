package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;

@Entity
@DiscriminatorValue("MANAGEMENT")
@PrimaryKeyJoinColumn(name = "access_level_id")
@Table(name = "management", schema = "public")
@EntityListeners(EntitiesLogger.class)
public class ManagementEntity extends AccessLevelEntity {
    public ManagementEntity() {
    }

    public ManagementEntity(boolean isActive, UserEntity userEntity) {
        super(isActive, userEntity);
    }
}
