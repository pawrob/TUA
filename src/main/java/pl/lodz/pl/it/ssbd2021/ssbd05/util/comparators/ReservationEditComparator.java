package pl.lodz.pl.it.ssbd2021.ssbd05.util.comparators;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ReservationEditDTO;

public class ReservationEditComparator {

    private ReservationEditComparator() {

    }

    public static boolean compareData(ReservationEditDTO a, ReservationEditDTO b) {
        boolean same = a.getReservationTo().equals(b.getReservationTo());
        same = same && a.getReservationFrom().equals(b.getReservationFrom());
        return same;
    }
}
