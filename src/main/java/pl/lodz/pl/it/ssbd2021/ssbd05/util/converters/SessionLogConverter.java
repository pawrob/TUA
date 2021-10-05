package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.SessionLogDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.SessionLogEntity;

/**
 * Klasa udostępniająca metody konwersji pomiedzy obiektami typu SessionLogEntity oraz SessionLogDTO
 */
public class SessionLogConverter {
    //
//    public static SessionLogEntity sessionLogEntityFromDTO(SessionLogDTO sessionLogDTO, long userId){
//        return new SessionLogEntity(sessionLogDTO.getActionTimestamp(),sessionLogDTO.getIpAddress(),sessionLogDTO.isSuccessful(),
//                UserConverter.us`(sessionLogDTO.getUser()));
//    }

    /**
     * Metoda pozwalająca na konwersje obiektu typu SessionLogEntity na obiekt typu SessionLogDTO
     * @param sessionLogEntity
     * @return obiekt DTo reprezentujący wpis dotyczący informacji o sesjach użytkowników
     */
    public static SessionLogDTO sessionLogDTOFromEntity(SessionLogEntity sessionLogEntity) {
        return new SessionLogDTO(sessionLogEntity.getActionTimestamp(), sessionLogEntity.getIpAddress(), sessionLogEntity.isSuccessful(),
                UserConverter.userEntityToDTO(sessionLogEntity.getUser()));
    }

}
