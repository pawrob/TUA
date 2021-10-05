package pl.lodz.pl.it.ssbd2021.ssbd05.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEditDTO extends AbstractDTO {
    private OffsetDateTime reservationFrom;
    private OffsetDateTime reservationTo;

    public ReservationEditDTO(long id, Long version, OffsetDateTime reservationFrom,
                              OffsetDateTime reservationTo,
                              String comment) {
        super(id, version);
        this.reservationFrom = reservationFrom;
        this.reservationTo = reservationTo;
    }
}
