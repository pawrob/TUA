package pl.lodz.pl.it.ssbd2021.ssbd05.util.comparators;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferEditDTO;

public class OfferEditComparator {

    private OfferEditComparator() {

    }

    public static boolean compareData(OfferEditDTO a, OfferEditDTO b) {
        boolean same = true;//TODO odkomentować jeśli mają być edytowalne godziny dostępności //OfferAvailabilitiesComparator.compareData(a.getOfferAvailabilities(), b.getOfferAvailabilities());
        same = same && a.getTitle().equals(b.getTitle());
        same = same && a.getDescription().equals(b.getDescription());
        same = same && a.getValidTo().equals(b.getValidTo());
        same = same && a.getValidFrom().equals(b.getValidFrom());
        return same;
    }
}
