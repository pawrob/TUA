package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation.AccessLevel;

import javax.validation.constraints.NotNull;

/**
 * Klasa DTO zawierająca informację o poziomie dostępu
 */
@ToString(callSuper = true)
@AllArgsConstructor
public class AccessLevelDTO extends AbstractDTO {

    @Getter
    @Setter
    @NotNull
    @AccessLevel
    private String accessLevel;

    @Getter
    @Setter
    private boolean isActive;

    public AccessLevelDTO(long id, long version, String accessLevel, boolean isActive) {
        super(id, version);
        this.accessLevel = accessLevel;
        this.isActive = isActive;
    }

    public AccessLevelDTO() {
    }

}
