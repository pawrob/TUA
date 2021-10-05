package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.Getter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;

@Entity
@Getter
@DiscriminatorValue("CLIENT")
@PrimaryKeyJoinColumn(name = "access_level_id")
@Table(name = "client", schema = "public")
@EntityListeners(EntitiesLogger.class)
@NamedQueries({
        @NamedQuery(name = "ClientEntity.findByUser", query = "SELECT k FROM ClientEntity k WHERE k.user = :user")
})
public class ClientEntity extends AccessLevelEntity {
    public ClientEntity(boolean isActive, UserEntity userEntity) {
        super(isActive, userEntity);
    }

    public ClientEntity() {

    }
}
