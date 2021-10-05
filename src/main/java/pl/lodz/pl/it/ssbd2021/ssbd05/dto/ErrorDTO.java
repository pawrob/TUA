package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Klasa DTO zawierająca informację o błędzie
 */
@RequiredArgsConstructor(staticName = "of")
@Getter
public class ErrorDTO {
    @NotBlank
    private final String key;
}
