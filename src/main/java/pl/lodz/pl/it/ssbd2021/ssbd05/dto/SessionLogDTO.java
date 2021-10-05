package pl.lodz.pl.it.ssbd2021.ssbd05.dto;

import lombok.Data;
import lombok.ToString;

import java.time.OffsetDateTime;

@Data
@ToString(callSuper = true)
public class SessionLogDTO {

    private OffsetDateTime actionTimestamp;
    private String ipAddress;
    private boolean isSuccessful;
    private UserDTO user;


    public SessionLogDTO(OffsetDateTime actionTimestamp, String ipAddress, boolean isSuccessful, UserDTO user) {
        this.actionTimestamp = actionTimestamp;
        this.ipAddress = ipAddress;
        this.isSuccessful = isSuccessful;
        this.user = user;
    }

    public SessionLogDTO() {
    }
}
