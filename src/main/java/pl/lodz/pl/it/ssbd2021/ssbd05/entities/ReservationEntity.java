package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation", schema = "ssbd05")
@EntityListeners(EntitiesLogger.class)
@NamedQueries({
        @NamedQuery(name = "ReservationEntity.findByClient", query = "SELECT k FROM ReservationEntity k WHERE k.client = :client")
})
public class ReservationEntity extends AbstractEntity {
    private OffsetDateTime reservationFrom;
    private OffsetDateTime reservationTo;
    private Status status;
    private Integer rating;
    private String comment;
    private ClientEntity client;
    private OfferEntity offer;

    @Basic
    @Column(name = "reservation_from", nullable = false)
    public OffsetDateTime getReservationFrom() {
        return reservationFrom;
    }

    @Basic
    @Column(name = "reservation_to", nullable = false)
    public OffsetDateTime getReservationTo() {
        return reservationTo;
    }

    @Basic
    @Column(name = "status", nullable = true, length = 20)
    @Enumerated(value = EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    @Basic
    @Column(name = "rating", nullable = true)
    public Integer getRating() {
        return rating;
    }

    @Basic
    @Column(name = "comment", nullable = true, length = 250)
    public String getComment() {
        return comment;
    }

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "access_level_id", nullable = false)
    public ClientEntity getClient() {
        return client;
    }

    @ManyToOne
    @JoinColumn(name = "offer_id", referencedColumnName = "id", nullable = false)
    public OfferEntity getOffer() {
        return offer;
    }

    public enum Status {
        PENDING, ACCEPTED, CANCELED, ENDED
    }
}
