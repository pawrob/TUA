package pl.lodz.pl.it.ssbd2021.ssbd05.entities;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "authentication_view", schema = "ssbd05")
public class AuthenticationViewEntity {
    private Long id;
    private String login;
    private String password;
    private String accessLevel;

    @Id
    @Column(name = "id", nullable = false, insertable = false)
    public Long getId() {
        return id;
    }

    @Basic
    @Column(name = "login", nullable = true, length = 16, updatable = false, insertable = false)
    public String getLogin() {
        return login;
    }

    @Basic
    @Column(name = "password", nullable = true, length = 60, updatable = false, insertable = false)
    public String getPassword() {
        return password;
    }

    @Basic
    @Column(name = "access_level", nullable = true, length = 16, updatable = false, insertable = false)
    public String getAccessLevel() {
        return accessLevel;
    }
}
