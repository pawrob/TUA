package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation.Password;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Klasa DTO zawierająca informację o starej i nowej wersji hasła w celu jego zmiany
 */
@ToString(callSuper = true)
public class PasswordChangeDTO {

    @Getter
    @Setter
    @NotNull
    @Password
    private String newPassword;

    @Getter
    @Setter
    @NotBlank
    private String oldPassword;

    public PasswordChangeDTO() {
    }
}
