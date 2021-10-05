package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.*;

@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NamedQueries({
        @NamedQuery(name = "PersonalDataEntity.findByNameOrSurname", query = "SELECT p FROM PersonalDataEntity p WHERE LOWER(p.name) LIKE LOWER(:name) or LOWER(p.surname) LIKE LOWER(:name)")
})
@Entity
@Table(name = "personal_data", schema = "public")
public class PersonalDataEntity {
    private long userId;
    private long version;

    private String name;
    private String surname;
    private String phoneNumber;
    private String language = "POL";
    private Boolean isMan;
    private UserEntity user;

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "user_id", nullable = false)
    public long getUserId() {
        return userId;
    }

    @Basic
    @Version
    @Column(name = "version", nullable = false)
    public long getVersion() {
        return version;
    }


    @Basic
    @Column(name = "name", nullable = true, length = 30)
    public String getName() {
        return name;
    }

    @Basic
    @Column(name = "surname", nullable = true, length = 30)
    public String getSurname() {
        return surname;
    }

    @Basic
    @Column(name = "phone_number", nullable = true, length = 15)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Basic
    @Column(name = "language", nullable = false, length = 3)
    public String getLanguage() {
        return language;
    }

    @Basic
    @Column(name = "is_man", nullable = true)
    public Boolean getIsMan() {
        return isMan;
    }

    @OneToOne(optional = false)
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
    public UserEntity getUser() {
        return user;
    }

    public PersonalDataEntity(long userId, String name, String surname, String phoneNumber, String language, Boolean isMan, long version) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.language = language;
        this.isMan = isMan;
        this.version = version;
    }

    public PersonalDataEntity() {
    }
}
