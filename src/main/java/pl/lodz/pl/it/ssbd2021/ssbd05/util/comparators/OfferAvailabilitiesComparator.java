package pl.lodz.pl.it.ssbd2021.ssbd05.util.comparators;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferAvailabilityDTO;

import java.util.Collection;
import java.util.stream.Collectors;

public class OfferAvailabilitiesComparator {

    private OfferAvailabilitiesComparator() {

    }

    public static boolean compareData(Collection<OfferAvailabilityDTO> a, Collection<OfferAvailabilityDTO> b) {
        var aList = a.stream().map(offerAvailabilityDTO -> {
            StringBuilder x = new StringBuilder();
            x
                    .append(offerAvailabilityDTO.getHoursFrom().toString())
                    .append(offerAvailabilityDTO.getHoursTo().toString())
                    .append(offerAvailabilityDTO.getWeekDay());
            return x.toString();
        }).collect(Collectors.toList());
        var bList = b.stream().map(offerAvailabilityDTO -> {
            StringBuilder x = new StringBuilder();
            x
                    .append(offerAvailabilityDTO.getHoursFrom().toString())
                    .append(offerAvailabilityDTO.getHoursTo().toString())
                    .append(offerAvailabilityDTO.getWeekDay());
            return x.toString();
        }).collect(Collectors.toList());
        if (aList.size() != bList.size()) {
            return false;
        }
        for (var aItem : aList) {
            if (!bList.contains(aItem)) {
                return false;
            }
        }
        return true;
    }
}
