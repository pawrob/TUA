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
public class OfferToCreateDTO extends AbstractDTO {

    private String title;

    private String description;

    private boolean isActive;

    private OffsetDateTime validFrom;

    private OffsetDateTime validTo;


}
