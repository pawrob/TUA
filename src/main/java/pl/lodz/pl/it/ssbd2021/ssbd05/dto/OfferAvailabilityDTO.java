package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetTime;

@NoArgsConstructor
public class OfferAvailabilityDTO extends AbstractDTO {

    @Getter
    @Setter
    private int weekDay;

    @Getter
    @Setter
    private OffsetTime hoursFrom;

    @Getter
    @Setter
    private OffsetTime hoursTo;

    public OfferAvailabilityDTO(long id, Long version, int weekDay, OffsetTime hoursFrom, OffsetTime hoursTo) {
        super(id, version);
        this.weekDay = weekDay;
        this.hoursFrom = hoursFrom;
        this.hoursTo = hoursTo;
    }
}
