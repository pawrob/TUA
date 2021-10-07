package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.Setter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Setter
@Entity
@DiscriminatorValue("ENTERTAINER")
@PrimaryKeyJoinColumn(name = "access_level_id")
@Table(name = "entertainer", schema = "ssbd05")
@EntityListeners(EntitiesLogger.class)
public class EntertainerEntity extends AccessLevelEntity {
    private String description="";
    private Double avgRating;
    private Collection<EntertainerUnavailabilityEntity> entertainerUnavailability = new ArrayList<>();

    @Basic
    @Column(name = "description", nullable = true, length = 2048)
    public String getDescription() {
        return description;
    }

    @Basic
    @Column(name = "avg_rating", nullable = true, precision = 0)
    public Double getAvgRating() {
        return avgRating;
    }

    @OneToMany
    @JoinColumn(name = "entertainer_id")
    public Collection<EntertainerUnavailabilityEntity> getEntertainerUnavailability() {
        return entertainerUnavailability;
    }

    public EntertainerEntity() {
    }

    public EntertainerEntity(boolean isActive, UserEntity userEntity) {
        super(isActive, userEntity);
    }
}
