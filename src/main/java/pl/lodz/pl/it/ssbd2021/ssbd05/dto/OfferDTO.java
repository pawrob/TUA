package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class OfferDTO extends AbstractDTO {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    @NotNull
    private boolean isActive;

    private Timestamp validFrom;

    private Timestamp validTo;

    private Double avgRating;

    private boolean isFavourite;


    public OfferDTO(long id, Long version, @NotBlank String title, String description,
                    @NotBlank @NotNull boolean isActive, Timestamp validFrom,
                    Timestamp validTo, Double avgRating, boolean isFavourite) {
        super(id, version);
        this.title = title;
        this.description = description;
        this.isActive = isActive;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.avgRating = avgRating;
        this.isFavourite = isFavourite;
    }

    public OfferDTO(long id, Long version, @NotBlank String title, String description,
                    @NotBlank @NotNull boolean isActive, Timestamp validFrom,
                    Timestamp validTo, Double avgRating) {
        super(id, version);
        this.title = title;
        this.description = description;
        this.isActive = isActive;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.avgRating = avgRating;
    }

    public OfferDTO(@NotBlank String title, String description,
                    @NotBlank @NotNull boolean isActive, Timestamp validFrom,
                    Timestamp validTo, Double avgRating) {
        this.title = title;
        this.description = description;
        this.isActive = isActive;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.avgRating = avgRating;
    }

}