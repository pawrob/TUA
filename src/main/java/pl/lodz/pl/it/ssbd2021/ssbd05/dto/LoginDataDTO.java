package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.validation.constraints.NotBlank;


/**
 * Klasa DTO zawierająca informację o danych uwierzytelniających
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDataDTO {

    @NotBlank
    private String login;

    @NotBlank
    @JsonbTypeAdapter(HidingContentJsonbAdapter.class)
    private String password;
}
