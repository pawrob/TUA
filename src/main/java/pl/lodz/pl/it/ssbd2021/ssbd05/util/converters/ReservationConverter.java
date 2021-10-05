package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;


import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ReservationDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ReservationEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReservationConverter {
    private ReservationConverter() {
    }

    public static ReservationDTO reservationEntityToDTO(ReservationEntity e) {
        return new ReservationDTO(e.getId(), e.getVersion(),
                e.getReservationFrom(), e.getReservationTo(), e.getStatus().name(),
                e.getRating(), e.getComment(), e.getClient().getUser().getId(),e.getClient().getUser().getLogin(),
                e.getOffer().getId(),e.getOffer().getTitle(),e.getOffer().getEntertainer().getId(),e.getOffer().getEntertainer().getUser().getLogin());
    }

    public static List<ReservationDTO> createReservationListDTOFromEntity(Collection<ReservationEntity> entities) {
        return null == entities ? null : entities.stream()
                .filter(Objects::nonNull)
                .map(ReservationConverter::reservationEntityToDTO)
                .collect(Collectors.toList());
    }

    public static ReservationEntity createEntityFromDto(ReservationDTO reservation) {
        return new ReservationEntity(reservation.getReservationFrom(),
                reservation.getReservationTo(),
                ReservationEntity.Status.PENDING,
                reservation.getRating(),
                reservation.getComment(),
                null,
                null);
    }
}


