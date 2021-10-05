import {AbstractDTO} from "@/api/abstractDTO";

export class OfferDTO extends AbstractDTO {
    /**
     * @param {number} id
     * @param {number} version
     * @param {string} title
     * @param {boolean} active
     * @param {date} validFrom
     * @param {date} validTo
     * @param {number} avgRating
     * @param {string} description
     *
     */
    constructor(id, version, active, title, validFrom, validTo, avgRating, description) {
        super(id, version);
        this.title = title;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.active = active;
        this.avgRating = avgRating;
        this.description = description;
    }

    static fromResponse(offerDTO) {
        return new OfferDTO(
            offerDTO.id,
            offerDTO.version,
            offerDTO.active,
            offerDTO.title,
            offerDTO.validFrom,
            offerDTO.validTo,
            offerDTO.avgRating,
            offerDTO.description
        )
            ;
    }
}