package pl.lodz.pl.it.ssbd2021.ssbd05.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEditDTO extends AbstractDTO {
    private Timestamp reservationFrom;
    private Timestamp reservationTo;

    public ReservationEditDTO(long id, Long version, Timestamp reservationFrom,
                              Timestamp reservationTo,
                              String comment) {
        super(id, version);
        this.reservationFrom = reservationFrom;
        this.reservationTo = reservationTo;
    }
}
