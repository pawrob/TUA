package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Getter
@Setter
public class EntertainerUnavailabilityDTO extends AbstractDTO{

    public EntertainerUnavailabilityDTO(long id, Long version, Timestamp dateTimeFrom, Timestamp dateTimeTo, String description) {
        super(id, version);
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
        this.description = description;
    }

    private Timestamp dateTimeFrom;
    private Timestamp dateTimeTo;
    private String description;
}
