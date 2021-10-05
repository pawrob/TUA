package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Data;

/**
 * Klasa DTO zawierająca informację o użytkowniku, jego poziomach dostępu i danych personalnych
 */
@Data
public class UserWithPersonalDataDTO extends UserDTO {
    private PersonalDataDTO personalData;

    public static final class Builder {
        private long id;
        private long version;
        private String login;
        private String email;
        private boolean isActive;
        private boolean isVerified;
        private PersonalDataDTO personalData;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public Builder login(String login) {
            this.login = login;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder isVerified(boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }

        public Builder personalData(PersonalDataDTO personalData) {
            this.personalData = personalData;
            return this;
        }

        public UserWithPersonalDataDTO build() {
            UserWithPersonalDataDTO user = new UserWithPersonalDataDTO();
            user.setId(id);
            user.setVersion(version);
            user.setLogin(login);
            user.setEmail(email);
            user.setActive(isActive);
            user.setVerified(isVerified);
            user.setPersonalData(personalData);
            return user;
        }
    }
}
