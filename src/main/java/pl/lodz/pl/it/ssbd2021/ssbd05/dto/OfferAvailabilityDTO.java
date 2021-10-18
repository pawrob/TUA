package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@NoArgsConstructor
public class OfferAvailabilityDTO extends AbstractDTO {

    @Getter
    @Setter
    private int weekDay;

    @Getter
    @Setter
    private Time hoursFrom;

    @Getter
    @Setter
    private Time hoursTo;

    public OfferAvailabilityDTO(long id, Long version, int weekDay, Time hoursFrom, Time hoursTo) {
        super(id, version);
        this.weekDay = weekDay;
        this.hoursFrom = hoursFrom;
        this.hoursTo = hoursTo;
    }
}
