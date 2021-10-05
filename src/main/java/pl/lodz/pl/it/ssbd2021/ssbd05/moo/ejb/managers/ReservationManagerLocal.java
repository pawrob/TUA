package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ReservationEditDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ReservationEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ReservationManagerLocal extends ManagerLocal {

    List<ReservationEntity> getAllReservations() throws AbstractAppException;
    List<ReservationEntity> getSelfReservations() throws AbstractAppException;
    ReservationEntity getReservation(Long id) throws AbstractAppException;

    ReservationEntity createReservation(ReservationEntity reservation, Long offerId, Long offerVersion) throws AbstractAppException;
    ReservationEntity updateReservation(Long id, ReservationEntity newReservation);
    ReservationEntity endReservation(Long id) throws AbstractAppException;

    ReservationEntity acceptReservation(Long id) throws AbstractAppException;

    ReservationEntity updateRating(Long id, int rating, String comment) throws AbstractAppException;
    ReservationEntity deleteRatingByClient(Long id) throws AbstractAppException;
    ReservationEntity deleteRatingByManagement(Long id) throws AbstractAppException;

    List<ReservationEntity> getAllReservationsForEntertainer();

    ReservationEntity editReservation(Long id, ReservationEditDTO reservationEditDTO) throws AbstractAppException;

    ReservationEntity clientCancelReservation(Long id) throws AbstractAppException;

    ReservationEntity entertainerCancelReservation(Long id) throws AbstractAppException;
}
