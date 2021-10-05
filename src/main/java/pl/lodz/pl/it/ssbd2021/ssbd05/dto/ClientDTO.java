package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

public class ClientDTO extends UserDTO {

    public ClientDTO(long id, long version, String login, String email, boolean isActive, boolean isVerified) {
        super(id, version, login, email, isActive, isVerified);
    }
}
