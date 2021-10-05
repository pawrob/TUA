package pl.lodz.pl.it.ssbd2021.ssbd05.util.converters;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ReservationEditDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.ReservationEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReservationEditConverter {


    private ReservationEditConverter() {
    }

    public static ReservationEditDTO reservationEntityToDTO(ReservationEntity entity) {
        return new ReservationEditDTO(entity.getId(), entity.getVersion(),
                entity.getReservationFrom(), entity.getReservationTo(), entity.getComment()
        );
    }

    public static List<ReservationEditDTO> createReservationListDTOFromEntity(Collection<ReservationEntity> entities) {
        return null == entities ? null : entities.stream()
                .filter(Objects::nonNull)
                .map(ReservationEditConverter::reservationEntityToDTO)
                .collect(Collectors.toList());
    }
}
