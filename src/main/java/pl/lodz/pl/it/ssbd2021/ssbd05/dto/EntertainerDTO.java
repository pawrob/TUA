package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * Klasa DTO zawierająca informację o pracowniku
 */
@NoArgsConstructor
@Data
public class EntertainerDTO extends UserDTO {
    @Size(max = 2048)
    private String description;
    private Double avgRating;
    private boolean isRoleActive;

    public EntertainerDTO(String login, String email, String description, Double avgRating) {
        super(login, email);
        isRoleActive = true;
        this.description = description;
        this.avgRating = avgRating;
    }

    public EntertainerDTO(String login, String email, boolean isActive,
                          boolean isVerified, String description, Double avgRating, boolean isRoleActive) {
        super(login, email, isActive, isVerified);
        this.description = description;
        this.avgRating = avgRating;
        this.isRoleActive = isRoleActive;
    }

    public EntertainerDTO(long id, long version, String login, String email,
                          boolean isActive, boolean isVerified,
                          String description, Double avgRating, boolean isRoleActive) {
        super(id, version, login, email, isActive, isVerified);
        this.description = description;
        this.avgRating = avgRating;
        this.isRoleActive = isRoleActive;
    }
}

