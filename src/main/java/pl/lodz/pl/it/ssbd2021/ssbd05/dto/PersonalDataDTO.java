package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.*;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation.NameOrSurname;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation.PhoneNumber;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Klasa DTO zawierająca informację o danych personalnych użytkownika
 */
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDataDTO extends AbstractDTO {

    @Getter
    @Setter
    @Size(max = 30)
    @NameOrSurname
    private String name;

    @Getter
    @Setter
    @Size(max = 30)
    @NameOrSurname
    private String surname;

    @Getter
    @Setter
    @Size(max = 15)
    @PhoneNumber
    private String phoneNumber;

    @Getter
    @Setter
    @NotNull
    @Size(max = 3)
    private String language;

    @Getter
    @Setter
    private Boolean isMan;


    public static final class Builder {
        private long id;
        private long version;
        private String name;
        private String surname;
        private String phoneNumber;
        private String language;
        private Boolean isMan;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder isMan(Boolean isMan) {
            this.isMan = isMan;
            return this;
        }

        public PersonalDataDTO build() {
            PersonalDataDTO personalData = new PersonalDataDTO();
            personalData.setId(id);
            personalData.setVersion(version);
            personalData.setName(name);
            personalData.setSurname(surname);
            personalData.setPhoneNumber(phoneNumber);
            personalData.setLanguage(language);
            personalData.setIsMan(isMan);
            return personalData;
        }
    }
}
