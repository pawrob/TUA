package pl.lodz.pl.it.ssbd2021.ssbd05.moo.ejb.managers;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferEditDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.dto.OfferToCreateDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.ejb.manager.ManagerLocal;
import pl.lodz.pl.it.ssbd2021.ssbd05.entities.OfferEntity;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.AbstractAppException;

import javax.ejb.Local;
import java.util.List;


@Local
public interface OfferManagerLocal extends ManagerLocal {
    List<OfferEntity> getAllOffers() throws AbstractAppException;

    OfferEntity getOffer(Long id) throws AbstractAppException;

    List<OfferEntity> getAllActiveOffers() throws AbstractAppException;

    List<OfferEntity> getAllEntertainerOffers() throws AbstractAppException;

    OfferEntity createOffer(OfferToCreateDTO offer) throws AbstractAppException;

    OfferEntity updateOffer(Long id, OfferEntity newOffer);

    OfferEntity deactivateOffer(Long id) throws AbstractAppException;

    OfferEntity activateOffer(Long id);

    OfferEntity changeActiveOffer(Long id, boolean active) throws AbstractAppException;

    OfferEntity addOfferToFavourites(Long id) throws AbstractAppException;

    OfferEntity deleteOfferFromFavourites(Long id) throws AbstractAppException;

    List<OfferEntity> getFavouriteOffers() throws AbstractAppException;

    OfferEntity editOffer(Long id, OfferEditDTO offerEditDTO) throws AbstractAppException;
}
