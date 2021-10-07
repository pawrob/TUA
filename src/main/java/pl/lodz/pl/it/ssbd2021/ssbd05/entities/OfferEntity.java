package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.pl.it.ssbd2021.ssbd05.util.logger.EntitiesLogger;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Setter
@Entity
@NoArgsConstructor
@Table(name = "offer", schema = "ssbd05")
@NamedQueries({
        @NamedQuery(name = "OfferEntity.findAllFavouritesOffers",
                query = "SELECT k FROM OfferEntity k WHERE :clientid in (SELECT c.id FROM k.likedBy c)")
})
@EntityListeners({EntitiesLogger.class})
public class OfferEntity extends AbstractEntity {
    private String title;
    private String description;
    private boolean isActive;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private Double avgRating;
    private Collection<ClientEntity> likedBy = new ArrayList<>();
    private EntertainerEntity entertainer;
    private Collection<OfferAvailabilityEntity> offerAvailabilities = new ArrayList<>();
    private Collection<ReservationEntity> reservations = new ArrayList<>();

    @Basic
    @Column(name = "title", nullable = false, length = 100)
    public String getTitle() {
        return title;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 350)
    public String getDescription() {
        return description;
    }

    @Basic
    @Column(name = "is_active", nullable = false)
    public boolean isActive() {
        return isActive;
    }

    @Basic
    @Column(name = "valid_from", nullable = false)
    public OffsetDateTime getValidFrom() {
        return validFrom;
    }

    @Basic
    @Column(name = "valid_to", nullable = false)
    public OffsetDateTime getValidTo() {
        return validTo;
    }

    @Basic
    @Column(name = "avg_rating", nullable = true, precision = 0)
    public Double getAvgRating() {
        return avgRating;
    }

    @ManyToMany
    @JoinTable(
            name = "favourites",
            joinColumns = @JoinColumn(name = "offer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "client_id", referencedColumnName = "access_level_id")
    )
    public Collection<ClientEntity> getLikedBy() {
        return likedBy;
    }

    @ManyToOne
    @JoinColumn(name = "entertainer_id", referencedColumnName = "access_level_id", nullable = false)
    public EntertainerEntity getEntertainer() {
        return entertainer;
    }

    @OneToMany
    @JoinColumn(name = "offer_id", referencedColumnName = "id")
    public Collection<OfferAvailabilityEntity> getOfferAvailabilities() {
        return offerAvailabilities;
    }

    @OneToMany(mappedBy = "offer")
    public Collection<ReservationEntity> getReservations() {
        return reservations;
    }

    public OfferEntity(String title, String description, boolean isActive, OffsetDateTime validFrom,
                       OffsetDateTime validTo, EntertainerEntity entertainer) {
        this.title = title;
        this.description = description;
        this.isActive = isActive;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.entertainer = entertainer;
    }
}
