package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AccessLevelLogDTO {
    @Getter
    @Setter
    @NotBlank
    private String login;


    @Getter
    @Setter
    @NotNull
    private String accessLevel;

    public AccessLevelLogDTO() {
    }

    public AccessLevelLogDTO(@NotBlank String login, @NotNull String accessLevel) {
        this.login = login;
        this.accessLevel = accessLevel;
    }
}
