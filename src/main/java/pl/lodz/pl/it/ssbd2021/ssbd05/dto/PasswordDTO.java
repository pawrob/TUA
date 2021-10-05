package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation.Password;

/**
 * Klasa DTO zawierająca informację o haśle
 */
@ToString(callSuper = true)
public class PasswordDTO {

    @Getter
    @Setter
    @Password
    private String password;

    public PasswordDTO() {
    }
}
