package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation.Email;

import java.util.List;


/**
 * Klasa DTO zawierająca informację o użytkowniku i jego poziomach dostępu
 */
@ToString(callSuper = true)
@AllArgsConstructor
public class UserWithAccessLevelDTO extends AbstractDTO {
    @Getter
    @Setter
    private String login;

    @Getter
    @Setter
    @Email
    private String email;

    @Getter
    @Setter
    private boolean isActive;

    @Getter
    @Setter
    private boolean isVerified;

    @Getter
    @Setter
    @ToString.Exclude
    private List<AccessLevelDTO> accessLevels;

    public UserWithAccessLevelDTO() {
    }

    public UserWithAccessLevelDTO(long id, long version, String login, String email, boolean isActive, boolean isVerified, List<AccessLevelDTO> accessLevels) {
        super(id, version);
        this.login = login;
        this.email = email;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.accessLevels = accessLevels;
    }

}
