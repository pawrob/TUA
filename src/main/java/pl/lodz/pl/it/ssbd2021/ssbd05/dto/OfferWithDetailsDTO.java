package pl.lodz.pl.it.ssbd2021.ssbd05.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Collection;

@Getter
@Setter
public class OfferWithDetailsDTO extends AbstractDTO {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    @NotNull
    private boolean isActive;

    private Timestamp validFrom;

    private Timestamp validTo;

    private Double avgRating;

    private Collection<ClientDTO> likedBy;

    private EntertainerDTO entertainer;

    private Collection<OfferAvailabilityDTO> offerAvailabilities;

    private Collection<ReservationDTO> reservations;


    public OfferWithDetailsDTO(long id, Long version, @NotBlank String title, String description,
                               @NotBlank @NotNull boolean isActive, Timestamp validFrom,
                               Timestamp validTo, Double avgRating, Collection<ClientDTO> likedBy, EntertainerDTO entertainer,
                               Collection<OfferAvailabilityDTO> offerAvailabilities, Collection<ReservationDTO> reservations) {
        super(id, version);
        this.title = title;
        this.description = description;
        this.isActive = isActive;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.avgRating = avgRating;
        this.likedBy = likedBy;
        this.entertainer = entertainer;
        this.offerAvailabilities = offerAvailabilities;
        this.reservations = reservations;
    }
}
