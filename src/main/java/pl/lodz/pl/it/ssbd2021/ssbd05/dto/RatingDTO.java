package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO extends AbstractDTO {
    @Min(0)
    @Max(5)
    private int rating;

    @Size(max = 250)
    private String comment;

}
