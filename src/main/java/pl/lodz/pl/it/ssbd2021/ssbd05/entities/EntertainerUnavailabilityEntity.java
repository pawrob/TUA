package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Setter
@Entity
@Table(name = "entertainer_unavailability", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(EntitiesLogger.class)
public class EntertainerUnavailabilityEntity extends AbstractEntity {
    private OffsetDateTime dateTimeFrom;
    private OffsetDateTime dateTimeTo;
    private String description;
    private boolean isValid;
    private long entertainerId;

    @Basic
    @Column(name = "is_valid", nullable = false)
    public boolean isValid() {
        return isValid;
    }

    @Basic
    @Column(name = "date_time_from", nullable = false)
    public OffsetDateTime getDateTimeFrom() {
        return dateTimeFrom;
    }

    @Basic
    @Column(name = "date_time_to", nullable = false)
    public OffsetDateTime getDateTimeTo() {
        return dateTimeTo;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 350)
    public String getDescription() {
        return description;
    }

    @Basic
    @Column(name = "entertainer_id", nullable = false)
    public long getEntertainerId() {
        return entertainerId;
    }
}
