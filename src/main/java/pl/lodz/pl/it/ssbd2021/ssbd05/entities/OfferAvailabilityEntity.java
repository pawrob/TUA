package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.Setter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;
import java.sql.Time;

@Setter
@Entity
@Table(name = "offer_availability", schema = "ssbd05")
@EntityListeners(EntitiesLogger.class)
public class OfferAvailabilityEntity extends AbstractEntity {
    private int weekDay;
    private Time hoursFrom;
    private Time hoursTo;

    @Basic
    @Column(name = "week_day", nullable = false)
    public int getWeekDay() {
        return weekDay;
    }

    @Basic
    @Column(name = "hours_from", nullable = false)
    public Time getHoursFrom() {
        return hoursFrom;
    }

    @Basic
    @Column(name = "hours_to", nullable = false)
    public Time getHoursTo() {
        return hoursTo;
    }
}
