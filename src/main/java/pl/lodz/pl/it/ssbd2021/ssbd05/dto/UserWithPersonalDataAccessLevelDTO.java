package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserWithPersonalDataAccessLevelDTO extends UserWithAccessLevelDTO {
    private PersonalDataDTO personalData;

    public UserWithPersonalDataAccessLevelDTO(String login, String email, boolean isActive, boolean isVerified, List<AccessLevelDTO> accessLevels, PersonalDataDTO personalData) {
        super(login, email, isActive, isVerified, accessLevels);
        this.personalData = personalData;
    }

    public UserWithPersonalDataAccessLevelDTO(PersonalDataDTO personalData) {
        this.personalData = personalData;
    }

    @Builder
    public UserWithPersonalDataAccessLevelDTO(long id, long version, String login, String email, boolean isActive, boolean isVerified, List<AccessLevelDTO> accessLevels, PersonalDataDTO personalData) {
        super(id, version, login, email, isActive, isVerified, accessLevels);
        this.personalData = personalData;
    }
}
