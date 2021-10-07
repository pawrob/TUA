package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.Setter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;
import java.time.OffsetTime;

@Setter
@Entity
@Table(name = "offer_availability", schema = "ssbd05")
@EntityListeners(EntitiesLogger.class)
public class OfferAvailabilityEntity extends AbstractEntity {
    private int weekDay;
    private OffsetTime hoursFrom;
    private OffsetTime hoursTo;

    @Basic
    @Column(name = "week_day", nullable = false)
    public int getWeekDay() {
        return weekDay;
    }

    @Basic
    @Column(name = "hours_from", nullable = false)
    public OffsetTime getHoursFrom() {
        return hoursFrom;
    }

    @Basic
    @Column(name = "hours_to", nullable = false)
    public OffsetTime getHoursTo() {
        return hoursTo;
    }
}
