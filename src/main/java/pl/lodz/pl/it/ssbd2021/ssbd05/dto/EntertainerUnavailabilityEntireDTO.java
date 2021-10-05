package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EntertainerUnavailabilityEntireDTO extends AbstractDTO {
    private OffsetDateTime dateTimeFrom;
    private OffsetDateTime dateTimeTo;
    private String description;
    private boolean isValid;
    private long entertainerId;
}
