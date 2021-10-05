package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation.Email;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Klasa DTO zawierająca informację o użytkowniku
 */
@ToString(callSuper = true)
public class UserDTO extends AbstractDTO {

    @Getter
    @Setter
    @NotBlank
    private String login;

    @Getter
    @Setter
    @NotNull
    @Email
    private String email;

    @Getter
    @Setter
    private boolean isActive;

    @Getter
    @Setter
    private boolean isVerified;

    public UserDTO() {
    }

    public UserDTO(String login, String email) {
        this.login = login;
        this.email = email;

    }

    public UserDTO(String login, String email, boolean isActive, boolean isVerified) {
        this.login = login;
        this.email = email;
        this.isActive = isActive;
        this.isVerified = isVerified;
    }

    public UserDTO(long id, long version, String login, String email, boolean isActive, boolean isVerified) {
        super(id, version);
        this.login = login;
        this.email = email;
        this.isActive = isActive;
        this.isVerified = isVerified;
    }
}
