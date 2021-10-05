package pl.lodz.pl.it.ssbd2021.ssbd05.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO extends AbstractDTO {

    private OffsetDateTime reservationFrom;
    private OffsetDateTime reservationTo;
    private String status;
    private Integer rating;
    private String comment;
    private long client;
    private String clientLogin;
    private long offer;
    private String offerTitle;
    private long entertainer;
    private String entertainerLogin;
    @JsonbTypeAdapter(VersionCryptJsonbAdapter.class)
    private Long offerVersion;

    public ReservationDTO(long id, Long version, OffsetDateTime reservationFrom,
                          OffsetDateTime reservationTo, String status, Integer rating,
                          String comment, long clientId, String clientLogin, long offerId, String offerTitle, long entertainer, String entertainerLogin) {
        super(id, version);
        this.reservationFrom = reservationFrom;
        this.reservationTo = reservationTo;
        this.status = status;
        this.rating = rating;
        this.comment = comment;
        this.client = clientId;
        this.offer = offerId;
        this.offerTitle = offerTitle;
        this.clientLogin = clientLogin;
        this.entertainerLogin = entertainerLogin;
        this.entertainer = entertainer;
    }
}
